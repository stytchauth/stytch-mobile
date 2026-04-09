// Credit: https://www.reactnativecrossroads.com/posts/expo-plugin-add-spm-dependency/
// eslint-disable-next-line @typescript-eslint/no-require-imports
const { withXcodeProject } = require('@expo/config-plugins');
// eslint-disable-next-line @typescript-eslint/no-require-imports
const path = require('path');

// If STYTCH_REPO_ROOT is set (local dev), use it as the path to the checked-out repo.
// Otherwise fall back to the live GitHub URL
function resolveSpmUrl() {
  if (process.env.STYTCH_REPO_ROOT) {
    return path.join(process.env.STYTCH_REPO_ROOT, 'source', 'ios');
  }
  return 'https://github.com/stytchauth/stytch-ios';
}

const spmUrl = resolveSpmUrl();
const addSPMDependenciesToMainTarget = (config) =>
  withXcodeProject(config, (config) => {
    const repoName = 'stytch-mobile';
    const productName = 'StytchConsumerSDK';
    const xcodeProject = config.modResults;
    // Newer Xcode requires XCLocalSwiftPackageReference for local paths;
    // XCRemoteSwiftPackageReference only accepts valid git remote URLs.
    const isLocal = !!process.env.STYTCH_REPO_ROOT;
    const refType = isLocal ? 'XCLocalSwiftPackageReference' : 'XCRemoteSwiftPackageReference';
    const refKey = isLocal ? 'relativePath' : 'repositoryURL';

    if (!xcodeProject.hash.project.objects[refType]) {
      xcodeProject.hash.project.objects[refType] = {};
    }

    const packageReferenceUUID = xcodeProject.generateUuid();

    xcodeProject.hash.project.objects[refType][
      `${packageReferenceUUID} /* ${refType} "${repoName}" */`
    ] = {
      isa: refType,
      [refKey]: spmUrl,
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
      package: `${packageReferenceUUID} /* ${refType} "${repoName}" */`,
      productName: productName,
    };

    // update PBXProject
    const projectId = Object.keys(xcodeProject.hash.project.objects['PBXProject']).at(0);

    if (!xcodeProject.hash.project.objects['PBXProject'][projectId]['packageReferences']) {
      xcodeProject.hash.project.objects['PBXProject'][projectId]['packageReferences'] = [];
    }

    xcodeProject.hash.project.objects['PBXProject'][projectId]['packageReferences'] = [
      ...xcodeProject.hash.project.objects['PBXProject'][projectId]['packageReferences'],
      `${packageReferenceUUID} /* ${refType} "${repoName}" */`,
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
