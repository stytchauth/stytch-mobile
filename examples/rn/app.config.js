const { homedir } = require("os");

module.exports = {
  expo: {
    name: "rn",
    slug: "rn",
    version: "1.0.0",
    orientation: "portrait",
    icon: "./assets/images/icon.png",
    scheme: "rn",
    userInterfaceStyle: "automatic",
    ios: {
      supportsTablet: true,
      bundleIdentifier: "com.stytch.sdk.kmpmigration",
      infoPlist: {
        ITSAppUsesNonExemptEncryption: false,
      },
    },
    android: {
      adaptiveIcon: {
        backgroundColor: "#E6F4FE",
        foregroundImage: "./assets/images/android-icon-foreground.png",
        backgroundImage: "./assets/images/android-icon-background.png",
        monochromeImage: "./assets/images/android-icon-monochrome.png",
      },
      predictiveBackGestureEnabled: false,
      package: "com.stytch.sdk.kmpmigration",
    },
    web: {
      output: "static",
      favicon: "./assets/images/favicon.png",
    },
    plugins: [
      "expo-router",
      [
        "expo-splash-screen",
        {
          image: "./assets/images/splash-icon.png",
          imageWidth: 200,
          resizeMode: "contain",
          backgroundColor: "#ffffff",
          dark: {
            backgroundColor: "#000000",
          },
        },
      ],
      "expo-dev-client",
      [
        "expo-build-properties",
        {
          android: {
            extraMavenRepos: [
              {
                url: `${homedir()}/.m2/repository`,
              },
            ],
            packagingOptions: {
              exclude: ["META-INF/versions/9/OSGI-INF/MANIFEST.MF"],
            },
          },
          ios: {
            useFrameworks: "static",
          },
        },
      ],
      ["expo-apple-authentication"],
      "expo-secure-store",
      "expo-font",
      "expo-image",
      "expo-web-browser",
    ],
    experiments: {
      typedRoutes: true,
      reactCompiler: true,
    },
    extra: {
      router: {},
      eas: {
        projectId: "58480dbe-30b6-4b5b-bd55-118e02582cf7",
      },
    },
    owner: "stytch",
  },
};
