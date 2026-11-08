package com.example

import com.example.model.Language
import com.example.ui.GarageStrings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testLocalizationStrings() {
    val engName = GarageStrings.get("appTitle", Language.EN)
    val gujName = GarageStrings.get("appTitle", Language.GU)
    val hinName = GarageStrings.get("appTitle", Language.HI)

    assertEquals("GJ13 AUTO GARAGE", engName)
    assertEquals("GJ13 AUTO GARAGE", gujName)
    assertEquals("GJ13 AUTO GARAGE", hinName)

    assertTrue(GarageStrings.DEFAULT_SERVICES.isNotEmpty())
  }
}
