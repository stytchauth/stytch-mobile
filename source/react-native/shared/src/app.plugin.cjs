// Credit: https://www.reactnativecrossroads.com/posts/expo-plugin-add-spm-dependency/
const { withXcodeProject } = require('@expo/config-plugins');
const path = require('path');
const fs = require('fs');

// Walk up the directory tree looking for source/ios/Package.swift (local dev), fall back to github (live/prod)
function resolveSpmUrl() {
  let dir = __dirname;
  while (true) {
    if (fs.existsSync(path.join(dir, 'source', 'ios', 'Package.swift'))) {
      return path.join(dir, 'source', 'ios');
    }
    const parent = path.dirname(dir);
    if (parent === dir) break;
    dir = parent;
  }
  return 'https://github.com/stytchauth/stytch-ios';
}

const spmUrl = resolveSpmUrl();
const addSPMDependenciesToMainTarget = (config) =>
  withXcodeProject(config, (config) => {
    const repositoryUrl = spmUrl;
    const repoName = 'stytch-mobile';
    const productName = 'StytchConsumerSDK';
    const xcodeProject = config.modResults;
    const spmReferences = xcodeProject.hash.project.objects['XCRemoteSwiftPackageReference'];

    if (!spmReferences) {
      xcodeProject.hash.project.objects['XCRemoteSwiftPackageReference'] = {};
    }

    const packageReferenceUUID = xcodeProject.generateUuid();

    xcodeProject.hash.project.objects['XCRemoteSwiftPackageReference'][
      `${packageReferenceUUID} /* XCRemoteSwiftPackageReference "${repoName}" */`
    ] = {
      isa: 'XCRemoteSwiftPackageReference',
      repositoryURL: repositoryUrl,
    };

    // update XCSwiftPackageProductDependency
    const spmProducts = xcodeProject.hash.project.objects['XCSwiftPackageProductDependency'];

    if (!spmProducts) {
      xcodeProject.hash.project.objects['XCSwiftPackageProductDependency'] = {};
    }

    const packageUUID = xcodeProject.generateUuid();

    xcodeProject.hash.project.objects['XCSwiftPackageProductDependency'][
      `${packageUUID} /* ${productName} */`
    ] = {
      isa: 'XCSwiftPackageProductDependency',
      // from step before
      package: `${packageReferenceUUID} /* XCRemoteSwiftPackageReference "${repoName}" */`,
      productName: productName,
    };

    // update PBXProject
    const projectId = Object.keys(xcodeProject.hash.project.objects['PBXProject']).at(0);

    if (!xcodeProject.hash.project.objects['PBXProject'][projectId]['packageReferences']) {
      xcodeProject.hash.project.objects['PBXProject'][projectId]['packageReferences'] = [];
    }

    xcodeProject.hash.project.objects['PBXProject'][projectId]['packageReferences'] = [
      ...xcodeProject.hash.project.objects['PBXProject'][projectId]['packageReferences'],
      `${packageReferenceUUID} /* XCRemoteSwiftPackageReference "${repoName}" */`,
    ];

    // update PBXBuildFile
    const frameworkUUID = xcodeProject.generateUuid();

    xcodeProject.hash.project.objects['PBXBuildFile'][`${frameworkUUID}_comment`] =
      `${productName} in Frameworks`;
    xcodeProject.hash.project.objects['PBXBuildFile'][frameworkUUID] = {
      isa: 'PBXBuildFile',
      productRef: packageUUID,
      productRef_comment: productName,
    };

    // update PBXFrameworksBuildPhase
    const buildPhaseId = Object.keys(
      xcodeProject.hash.project.objects['PBXFrameworksBuildPhase'],
    ).at(0);

    if (!xcodeProject.hash.project.objects['PBXFrameworksBuildPhase'][buildPhaseId]['files']) {
      xcodeProject.hash.project.objects['PBXFrameworksBuildPhase'][buildPhaseId]['files'] = [];
    }

    xcodeProject.hash.project.objects['PBXFrameworksBuildPhase'][buildPhaseId]['files'] = [
      ...xcodeProject.hash.project.objects['PBXFrameworksBuildPhase'][buildPhaseId]['files'],
      `${frameworkUUID} /* ${productName} in Frameworks */`,
    ];

    return config;
  });

module.exports = addSPMDependenciesToMainTarget;
