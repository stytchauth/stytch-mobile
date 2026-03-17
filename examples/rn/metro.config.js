const { getDefaultConfig } = require('expo/metro-config');
const path = require('path');

const config = getDefaultConfig(__dirname);

const packageRoot = path.resolve(__dirname, '../../source/react-native/consumer');

config.watchFolders = [packageRoot];

// Force all React/React Native imports to resolve from the app's node_modules,
// regardless of which package is requiring them. This prevents duplicate React
// instances when using file: dependencies that have their own node_modules.
config.resolver.resolveRequest = (context, moduleName, platform) => {
  if (moduleName === 'react' || moduleName === 'react-native') {
    return {
      type: 'sourceFile',
      filePath: require.resolve(moduleName),
    };
  }
  return context.resolveRequest(context, moduleName, platform);
};

module.exports = config;
