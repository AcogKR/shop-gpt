package dev.acog.shop

import io.heartpattern.javagpt.Gpt

interface PriceGPT {

    @Gpt("Returns the entered value with a random discount")
    fun discountedPrice(price: Double) : Double

}

