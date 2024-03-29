# PubNub Chat Components for Android

> WARNING: PubNub no longer supports this software library, but you are welcome to contribute.

Chat components provide easy-to-use building blocks to create Android chat applications, for use
cases like live event messaging, telemedicine, and service desk support.

Our Android component library provides chat features like direct and group messaging, typing
indicators, presence, and reactions. You don't need to implement a data source by yourself and go
through the complexity of designing the architecture of a realtime network. Instead, use our
components, predefined repositories, and view models to create custom apps for a wide range of use
cases.

## Features

* **User and Channel Metadata**: Add additional information about the users, channels, and their
  memberships (and store it locally for offline use)
* **Subscriptions**: Subscribe to user channels automatically
* **Messages**: Publish and display new and historical text messages
* **Presence**: Get currently active users, observe their state, and notify about changes
* **Typing Indicators**: Display notifications that users are typing
* **Persistent Data Storage**: Store messages, channels, and users locally
* **Paging**: Pull new data only when you need it
* **Jetpack Compose**: Build native UI with a modern toolkit

## Available components

* [ChannelList](https://www.pubnub.com/docs/chat/community-supported/android/ui-components#channellist)
* [MemberList](https://www.pubnub.com/docs/chat/community-supported/android/ui-components#memberlist)
* [MessageList](https://www.pubnub.com/docs/chat/community-supported/android/ui-components#messagelist)
* [MessageInput](https://www.pubnub.com/docs/chat/community-supported/android/ui-components#messageinput)

## Prerequisites

| Name                                                           | Requirement                  |
|:---------------------------------------------------------------|:-----------------------------|
| [Android Studio](https://developer.android.com/studio/preview) | > = Dolphin 2021.3.1 Patch 1 |
| Platform                                                       | Android                      |
| Language                                                       | Kotlin                       |
| UI Toolkit                                                     | Jetpack Compose              |
| [PubNub Kotlin SDK](https://github.com/pubnub/kotlin)          | > = 7.3.2                    |

## Usage

### Install chat components

1. Inside your Android Studio project, open the module's `build.gradle` module and add both Kotlin
   SDK and PubNub Chat Components for Android implementations:

    ```kotlin
    implementation "com.pubnub:pubnub-kotlin:7.3.2"
    implementation "com.pubnub.components:chat-android:0.6.0"
    ```

2. Click **Sync Now** or **Sync Project with Gradle Files** to build dependencies for your Android
   project.

### Test sample apps

Explore [sample apps](https://github.com/pubnub/chat-components-android-examples/blob/master/README.md)
that are built using chat components.

Follow the steps in
the [Getting Started guide](https://www.pubnub.com/docs/chat/community-supported/android)
to set up a sample chat app and send your first message.

## Related documentation

* [PubNub Chat Components for Android Documentation](https://www.pubnub.com/docs/chat/community-supported/android)
* [Kotlin SDK Documentation](https://www.pubnub.com/docs/sdks/kotlin)
* [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
* [Android Room Tutorial](https://developer.android.com/training/data-storage/room)
* [Paging Library](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)

## Support

If you need help or have a general question, [contact support](mailto:support@pubnub.com).
