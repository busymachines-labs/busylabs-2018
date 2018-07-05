package main

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

object Main extends App {

  def scrapeNewsTitle() = {
    val browser = JsoupBrowser()
    val doc = browser.get("https://www.bbc.com/news")
    val list = doc >> element("#latest-stories-tab-container a h3") >> allText
    println(list)
  }

  scrapeNewsTitle()

}
