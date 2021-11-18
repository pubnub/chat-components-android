# Android Chat Components

Chat components provide easy-to-use building blocks to create Android chat applications, for use cases like live event messaging, telemedicine, and service desk support.

Our Android component library provides chat features like direct and group messaging, typing indicators, presence, and reactions. You don't need to implement a data source by yourself and go through the complexity of designing the architecture of a realtime network. Instead, use our components, predefined repositories, and view models to create custom apps for a wide range of use cases.

## Features

* **User and Channel Metadata**: add additional information about the users, channels, and their memberships (and store it locally for offline use)
* **Subscriptions**: subscribe to user channels automatically
* **Messages**: publish and display new and historical text messages
* **Presence**: get currently active users, observe their state, and notify about changes
* **Typing Indicators**: display notifications that users are typing
* **Persistent Data Storage**: store messages, channels, and users locally
* **Paging**: pull new data only when you need it
* **Jetpack Compose**: build native UI with a modern toolkit

## Available components

* [ChannelList](https://www.pubnub.com/docs/chat/components/android/ui-components-android#channellist)
* [MemberList](https://www.pubnub.com/docs/chat/components/android/ui-components-android#memberlist)
* [MessageList](https://www.pubnub.com/docs/chat/components/android/ui-components-android#messagelist)
* [MessageInput](https://www.pubnub.com/docs/chat/components/android/ui-components-android#messageinput)

## Related documentation

* [Kotlin SDK Documentation](https://www.pubnub.com/docs/sdks/kotlin)
* [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
* [Android Room Tutorial](https://developer.android.com/training/data-storage/room)
* [Paging Library](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)

## Prerequisites

| Name | Requirement |
| :--- | :------ |
| [Android Studio](https://developer.android.com/studio/) | >= Arctic Fox 2020.3.1 |
| Platform | Android |
| Language | Kotlin |
| UI Toolkit | Jetpack Compose |
| [PubNub Kotlin SDK](https://github.com/pubnub/kotlin) | >= 6.0.2 |

## Create a PubNub account

1. Sign in or set up an account to create an app on the [Admin Portal](https://dashboard.pubnub.com/). Create an app to get the keys you will need to use in your application.

1. When you create a new app, the first set of keys is generated automatically, but a single app can have as many keysets as you like. We recommend that you create separate keysets for production and test environments.

   :bulb: **Additional features**
   
      _Depending on your use case, you may want your application to have some PubNub features, such as Presence, Storage and Playback (including correct Retention), or Objects. To use them, you must first enable them on your Admin Portal keysets. If you decide to use Objects, be sure to select a geographic region corresponding to most users of your application._


## Create a new project for your app

1. Install and open [Android Studio](https://developer.android.com/studio/).

1. If you’re in the **Welcome to Android Studio** window, click **Start a new Android Studio project** or select **File** > **New** > **New Project** from the menu.

1. In the **Select a Project Template** window, choose **Empty Compose Activity** and click **Next**.

1. In the **Configure your project** window, fill in project details and set **Minimum SDK** to `API 21`. Click **Finish**.

1. Go to the project’s `build.gradle` file and update the Jetpack Compose version:

    ```kotlin
    compose_version = '1.0.2'
    ```

For any additional questions, refer to [Jetpack Compose documentation](https://developer.android.com/jetpack/compose/setup).
Check warning on line 45 in docs/components/android/get-started-android.md

## Install chat components

1. Inside your Android Studio project, open the module's `build.gradle` and add both Kotlin SDK and PubNub Chat Components for Android implementations:

    ```kotlin
    implementation "com.pubnub:pubnub-kotlin:6.2.0"
    implementation "com.pubnub.components:chat-android:0.1.0"
    ```

1. Click **Sync Now** to build dependencies for your Android project.

## Work with chat components

The first required step is to call `ChatProvider`, which initializes all the data components. These components are responsible for providing data to UI, setting the default theme, and communicating with the PubNub service. The best way to achieve it is by modifying the application theme functionality.

1. Open `MainActivity.kt` and create a configured PubNub instance. To do that, use your Publish and Subscribe Keys from your PubNub account dashboard on the Admin Portal.

    ```kotlin
    private val pubNub: PubNub = PubNub(
      PNConfiguration().apply {
        this.publishKey = "pub-c-key"
        this.subscribeKey = "sub-c-key"
      }
    )
    ```

   You can configure the UUID to associate a sender/current user with the PubNub messages.

    ```kotlin
    private val pubNub: PubNub = PubNub(
      PNConfiguration().apply {
        …
        this.uuid = "uuid-of-current-user"
        …
      }
    )
    ```

1. Destroy the `pubNub` instance in `onDestroy()`.

    ```kotlin
    override fun onDestroy() {
        super.onDestroy()
        pubNub.destroy()
    }
    ```

1. Open the `ui/Theme.kt` file, add the `pubNub` parameter, and initialize `ChatProvider` in `MyApplicationTheme`. This object is used to facilitate the majority of the functionality provided by PubNub Chat Components for Android.

    ```kotlin
      @Composable
      fun MyApplicationTheme(
          pubNub: PubNub,
          darkTheme: Boolean = isSystemInDarkTheme(),
          content: @Composable () -> Unit
      ) {
          val colors = if (darkTheme) DarkColorPalette
          else LightColorPalette
          MaterialTheme(
              colors = colors,
              typography = Typography,
              shapes = Shapes,
          ) {
              ChatProvider(pubnub) {
                  content()
              }
          }
      }
      ```

   For more information, refer to the [ChatProvider](https://www.pubnub.com/docs/chat/components/android/chat-provider-android) section.

1. Go back to `MainActivity.kt` and initialize `DefaultChannelListViewModel` inside the `setContent` block.

    ```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
        val viewModel: ChannelViewModel = ChannelViewModel.default()
      }
    }
    ```

1. Add the `ChannelList` component and pass `channels` as a parameter.

    ```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
        val viewModel: ChannelViewModel = ChannelViewModel.default()
        Box(modifier = Modifier.fillMaxSize()) {
            ChannelList(
                channels = viewModel.getAll(),
            )
        }
      }
    }
    ```

   At the end, `MainActivity.kt` should resemble the following:

    ```kotlin
    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.ui.Modifier
    import com.pubnub.api.PNConfiguration
    import com.pubnub.api.PubNub
    import com.pubnub.components.chat.ui.component.channel.ChannelList
    import com.pubnub.components.chat.viewmodel.channel.ChannelViewModel
    class MainActivity: ComponentActivity() {
        private val pubNub: PubNub = PubNub(
            PNConfiguration().apply {
                this.publishKey = "pub-c-key"
                this.subscribeKey = "sub-c-key"
                this.uuid = "uuid-of-current-user"
            }
        )
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                val viewModel: ChannelViewModel = ChannelViewModel.default()
                Box(modifier = Modifier.fillMaxSize()) {
                    ChannelList(
                        channels = viewModel.getAll(),
                    )
                }
            }
        }
        override fun onDestroy() {
            super.onDestroy()
            pubNub.destroy()
        }
    }
    ```

   When you open your application now, you should see the view displaying all the channels you previously specified.
