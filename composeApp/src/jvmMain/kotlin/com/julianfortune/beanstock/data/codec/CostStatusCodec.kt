package com.julianfortune.beanstock.data.codec

import com.julianfortune.beanstock.data.model.CostStatus
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success


object CostStatusCodec :
    Codec<CostStatus> {

    override fun deserialize(value: String): Result<CostStatus, CodecError> {
        return when (value) {
            "NO_COST" -> Success(CostStatus.NO_COST)
            "PURCHASED" -> Success(CostStatus.PURCHASED)
            else -> Failure(CodecError.InvalidInput(value))
        }
    }

    override fun serialize(value: CostStatus): String {
        return when (value) {
            CostStatus.NO_COST -> "NO_COST"
            CostStatus.PURCHASED -> "PURCHASED"
        }
    }
}
