package com.pubnub.framework.mapper

import com.pubnub.api.models.consumer.presence.PNHereNowChannelData
import com.pubnub.framework.data.OccupancyMap

// TODO: 6/7/21 move it! 
interface OccupancyMapper : Mapper<HashMap<String, PNHereNowChannelData>?, OccupancyMap?>
