package com.pubnub.framework.mapper

import com.pubnub.api.models.consumer.presence.PNHereNowChannelData
import com.pubnub.framework.data.OccupancyMap

interface OccupancyMapper : Mapper<HashMap<String, PNHereNowChannelData>?, OccupancyMap?>
