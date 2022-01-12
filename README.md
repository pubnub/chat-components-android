# PubNub Chat Components for Android

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

## Prerequisites

| Name | Requirement |
| :--- | :------ |
| [Android Studio](https://developer.android.com/studio/preview) | >= Bumblebee 2021.1.1 |
| Platform | Android |
| Language | Kotlin |
| UI Toolkit | Jetpack Compose |
| [PubNub Kotlin SDK](https://github.com/pubnub/kotlin) | >= 6.0.2 |

## Usage

Start by exploring our [sample apps](https://github.com/pubnub/chat-components-android-examples/blob/master/README.md) that are built using chat components.

Follow the steps in the [Getting Started guide](https://www.pubnub.com/docs/chat/components/android/get-started-android) to set up one of the sample chat apps and send your first message.

## Related documentation

* [PubNub Chat Components for Android Documentation](https://www.pubnub.com/docs/chat/components/android/overview-android)
* [Kotlin SDK Documentation](https://www.pubnub.com/docs/sdks/kotlin)
* [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
* [Android Room Tutorial](https://developer.android.com/training/data-storage/room)
* [Paging Library](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
