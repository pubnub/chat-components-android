package com.pubnub.components.chat.ui.component.menu

import com.pubnub.components.chat.ui.component.message.MessageUi
import com.pubnub.components.chat.ui.component.message.reaction.Reaction

sealed class MenuAction(val message: MessageUi.Data)

class Copy(message: MessageUi.Data): MenuAction(message)

class Edit(message: MessageUi.Data): MenuAction(message)

class Delete(message: MessageUi.Data): MenuAction(message)

class React(val reaction: Reaction, message: MessageUi.Data): MenuAction(message)
