package com.testautomation.StepDef;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.log4testng.Logger;

import com.testautomation.PageObjects.DSLCalculatorPage;
import com.testautomation.PageObjects.TariffListPage;
import com.testautomation.PageObjects.TariffOfferDetailPage;
import com.testautomation.PageObjects.VerivoxHomePage;
import com.testautomation.Utility.BrowserUtility;
import com.testautomation.Utility.PropertiesFileReader;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import junit.framework.Assert;

public class VerivoxUserTariffVerifyStepDef {

	public static WebDriver driver;
	int totalErmittelteTarife, maxTariffPerpage, totalTariffCurrentpage, tarrifPerPage, tariffProviderPrice;
	List<WebElement> totaltariffElement, allTariffProviderName;
	WebElement lasttariffElement, loadMoreTariffs;
	PropertiesFileReader obj = new PropertiesFileReader();
	Properties properties;
	Logger logger = Logger.getLogger(VerivoxUserTariffVerifyStepDef.class);
	WebDriverWait wait;
	String tariffProviderName, tariffProviderDescription, stringTariffProviderPrice;
	VerivoxHomePage homePageElements;
	DSLCalculatorPage dSLCalculatorPageElements;
	TariffListPage tariffListPage;
	TariffOfferDetailPage tariffOfferDetailPage;
	int minimumInternetSpeed;

	// Modified the feature file and code is taking Application Url and Browser Name
	// Dynamically
	// Applied assert statement at the end of every method to verify the particular requirement has passed or not.
	// Applied Page Object Model to show the framework more robust although most the WebElements are not reused. So that It can be use full in future. 

	@Given("that I can open {string} in {string}")
	public void that_I_can_open_in(String url, String browserName) {

		try {
			properties = obj.getProperty();
			driver = BrowserUtility.OpenBrowser(driver, url, browserName);
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			homePageElements = new VerivoxHomePage(driver);
			homePageElements.clickAcceptCookie();
			String PageTitle = driver.getTitle();
			Assert.assertEquals("Verivox Home Page is not Launched", properties.getProperty("browser.title"),
					PageTitle);
			logger.info(" that I can open https://www.verivox.de in Chrome method is passed ");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	@When("I navigate to the DSL calculator page")
	public void i_navigate_to_the_DSL_calculator_page() {
		homePageElements.clickDSLTab();
		Assert.assertTrue("Error in Clicking DSL Calculator Page Tab ",
				(driver.getTitle().equals(properties.getProperty("browser.DSLPage.title"))));
		logger.info(" i_navigate_to_the_DSL_calculator_page method is passed ");
	}

	@When("I enter {string} for my area code")
	public void i_enter_for_my_area_code(String areaCode) {
		dSLCalculatorPageElements = new DSLCalculatorPage(driver);
		dSLCalculatorPageElements.clickInternetPlanType();
		Assert.assertTrue("Error in Entering area code ", dSLCalculatorPageElements.sendKeysIntoAreaCode(areaCode));
		logger.info(" i_enter_for_my_area_code method is passed ");
	}

	@When("I select the {string} Mbit\\/s bandwidth option")
	public void i_select_the_Mbit_s_bandwidth_option(String internetSpeed) {
		minimumInternetSpeed = Integer.parseInt(internetSpeed);
		Assert.assertTrue("Error in selecting internet speed ",
				dSLCalculatorPageElements.clickInternetSpeed(internetSpeed));
		logger.info(" i_select_the_Mbit_s_bandwidth_option method is passed ");
	}

	@When("I click the Jetzt vergleichen button")
	public void i_click_the_Jetzt_vergleichen_button() {
		Assert.assertTrue("Error in clicking JetztVergleichenButton ",
				dSLCalculatorPageElements.clickJetztVergleichenButton());
		logger.info(" i_click_the_Jetzt_vergleichen_button method is passed ");
	}

	@Then("I should see a page that lists the available tariffs for my selection")
	public void i_should_see_a_page_that_lists_the_available_tariffs_for_my_selection() {
		int minimumTarrifDispalyed = Integer.parseInt(properties.getProperty("browser.DSLResultListpage.minimumTarrifsDisaplyed"));
		tariffListPage = new TariffListPage(driver);
		List<WebElement> tariffList = tariffListPage.appTariffPrice;
		Assert.assertTrue("Tarrif List Displayed are less than 5 ", tariffList.size() > minimumTarrifDispalyed);

		List<WebElement> priceElements = tariffListPage.internetSpeed;
		boolean isMinimumThreasholdMaintained = false;
		isMinimumThreasholdMaintained = BrowserUtility.isMinimumThreasholdMaintained(priceElements.size(),
				minimumInternetSpeed, driver);
		logger.info("Internet speed displayed is more than 100 Mbit/s:- " + isMinimumThreasholdMaintained);
		Assert.assertTrue("Internet speed is not more than 100 Mbit/s:- :- ", isMinimumThreasholdMaintained);
		logger.info(" i_should_see_a_page_that_lists_the_available_tariffs_for_my_selection method is passed ");
	}

	@Given("I should see the total number of available tariffs listed in the Ermittelte Tarife section")
	public void i_should_see_the_total_number_of_available_tariffs_listed_in_the_Ermittelte_Tarife_section() {
		tariffListPage = new TariffListPage(driver);
		totalErmittelteTarife = Integer.parseInt(tariffListPage.getErmittelteTarifeText().replaceAll("[^0-9]", ""));
		logger.info("Total number of available tariffs listed in the Ermittelte Tarife section are:- "
				+ totalErmittelteTarife);
		Assert.assertTrue("Total number of available tariffs are not displayed in the Ermittelte Tarife section",
				(totalErmittelteTarife > 0));
		logger.info(
				" i_should_see_the_total_number_of_available_tariffs_listed_in_the_Ermittelte_Tarife_section method is passed ");
	}

	@When("I scroll to the end of the Result List page")
	public void i_scroll_to_the_end_of_the_Result_List_page() {
		totaltariffElement = tariffListPage.ErmittelteTarifeList;
		maxTariffPerpage = tariffListPage.ErmittelteTarifeList.size();
		lasttariffElement = tariffListPage.getElementFromErmittelteTarifeList(maxTariffPerpage);
		Assert.assertTrue("Not able to scroll to the end of the Result List page",
				BrowserUtility.scrollIntoView(driver, lasttariffElement));
		logger.info(" i_scroll_to_the_end_of_the_Result_List_page method is passed ");
	}

	@Then("I should see only the first {int} tariffs displayed")
	public void i_should_see_only_the_first_tariffs_displayed(Integer sizeOfTariffPerPage) {
		tarrifPerPage = sizeOfTariffPerPage;
		Assert.assertTrue("Internet provider per page is more or less than 20",
				maxTariffPerpage == sizeOfTariffPerPage);
		totalErmittelteTarife = totalErmittelteTarife - maxTariffPerpage;
		logger.info("Total number of available tariffs remianing in the Ermittelte Tarife section are:- "
				+ totalErmittelteTarife);
		logger.info(" i_should_see_only_the_first_tariffs_displayed method is passed ");
	}

	@When("I click on the button labeled {int} weitere Tarife laden")
	public void i_click_on_the_button_labeled_weitere_Tarife_laden(Integer sizeOfTariffPerPage) {
		totalTariffCurrentpage = sizeOfTariffPerPage; // 20
		tariffListPage.clickLoadMoreTariffs(sizeOfTariffPerPage);
		totalErmittelteTarife = totalErmittelteTarife - maxTariffPerpage;
		System.out.println("Total number of available tariffs remianing in the Ermittelte Tarife section are:- "
				+ totalErmittelteTarife);
		logger.info(" i_click_on_the_button_labeled_weitere_Tarife_laden method is passed ");
	}

	@Then("I should see the next {int} tariffs displayed")
	public void i_should_see_the_next_tariffs_displayed(Integer sizeOfTariffPerPage) {
		StringBuilder xpathBuilder = new StringBuilder();
		xpathBuilder = xpathBuilder.append("(.//app-tariff)[" + (totalTariffCurrentpage + 1) + "]");
		String xpathElement = xpathBuilder.toString();
		
		// Designed Custom wait "waitForElementUsingXpath" as Webdriver wait is volatile and throws exception in some cases which is known and open issue.
		
		BrowserUtility.waitForElementUsingXpath(driver, xpathElement, 10);
		maxTariffPerpage = tariffListPage.ErmittelteTarifeList.size();
		logger.info("Number of tariffs displayed in the current page for Ermittelte Tarife section are:- "
				+ maxTariffPerpage);
		totalTariffCurrentpage = totalTariffCurrentpage + sizeOfTariffPerPage;
		Assert.assertTrue("Tariffs displayed after clicked on 20 weitere Tarife laden are more or less than "
				+ sizeOfTariffPerPage + " on current page", maxTariffPerpage == totalTariffCurrentpage);

		lasttariffElement = tariffListPage.getElementFromErmittelteTarifeList(maxTariffPerpage);
		BrowserUtility.scrollIntoView(driver, lasttariffElement);
		logger.info(" i_should_see_the_next_tariffs_displayed method is passed ");
	}

	@Then("I can continue to load any additional tariffs until all tariffs have been displayed")
	public void i_can_continue_to_load_any_additional_tariffs_until_all_tariffs_have_been_displayed() {

		while (totalErmittelteTarife > 0) {
			BrowserUtility.clickLoadMoreTariffs(tariffListPage, totalErmittelteTarife, tarrifPerPage);

			StringBuilder xpathBuilder = new StringBuilder();
			xpathBuilder = xpathBuilder.append("(.//app-tariff)[" + (totalTariffCurrentpage + 1) + "]");
			String xpathElement = xpathBuilder.toString();
			BrowserUtility.waitForElementUsingXpath(driver, xpathElement, 10);
			maxTariffPerpage = tariffListPage.ErmittelteTarifeList.size();

			if (totalErmittelteTarife >= tarrifPerPage) {
				Assert.assertTrue("Total tarrifs displayed are not as per requirement",
						maxTariffPerpage == totalTariffCurrentpage + tarrifPerPage);
				totalTariffCurrentpage = totalTariffCurrentpage + tarrifPerPage;
				totalErmittelteTarife = totalErmittelteTarife - tarrifPerPage;
				logger.info("Total number of available tariffs remianing in the Ermittelte Tarife section are:- "
						+ totalErmittelteTarife);
			} else if (totalErmittelteTarife > 0) {
				Assert.assertTrue("Total tarrifs displayed are not as per requirement",
						maxTariffPerpage == totalTariffCurrentpage + totalErmittelteTarife);
				totalTariffCurrentpage = totalTariffCurrentpage + totalErmittelteTarife;
				totalErmittelteTarife = totalErmittelteTarife - totalErmittelteTarife;
				logger.info("Total number of available tariffs remianing in the Ermittelte Tarife section are:- "
						+ totalErmittelteTarife);
			}

			lasttariffElement = tariffListPage.getElementFromErmittelteTarifeList(maxTariffPerpage);
			BrowserUtility.scrollIntoView(driver, lasttariffElement);
		}

		Assert.assertTrue(
				"The total number of tariffs displayed does not matche the total listed in the Ermittelte Tarife section",
				!BrowserUtility.assertWeiterButton(totalErmittelteTarife, maxTariffPerpage, totalTariffCurrentpage,
						tariffListPage));
		logger.info(
				" i_can_continue_to_load_any_additional_tariffs_until_all_tariffs_have_been_displayed method is passed ");
	}

	@Given("I click on any Zum Angebot button to select a tariff offer")
	public void i_click_on_any_Zum_Angebot_button_to_select_a_tariff_offer() {
		tariffListPage = new TariffListPage(driver);
		int TariffProviderCount = tariffListPage.totalTariffProviderName.size() - 1;
		tariffProviderName = tariffListPage.getTariffProviderName(TariffProviderCount);
		tariffProviderDescription = tariffListPage.getTariffProviderDescription(TariffProviderCount);
		tariffProviderPrice = tariffListPage.getTariffProviderPrice(TariffProviderCount + 1);
		Assert.assertTrue("Zum angebot button is not clicked",
				tariffListPage.clickZumAngebotButton(TariffProviderCount));
		logger.info(" i_click_on_any_Zum_Angebot_button_to_select_a_tariff_offer method is passed ");
	}

	@Then("I should see the corresponding offer page for the selected tariff")
	public void i_should_see_the_corresponding_offer_page_for_the_selected_tariff() {
		tariffOfferDetailPage = new TariffOfferDetailPage(driver);

		StringBuilder xpathBuilder = new StringBuilder();
		xpathBuilder = xpathBuilder.append(".//h1[contains(text(),'Angebot')]");
		String xpathElement = xpathBuilder.toString();
		BrowserUtility.waitForElementUsingXpath(driver, xpathElement, 10);

		Assert.assertTrue("OfferDetailElement is not Displayed ",
				tariffOfferDetailPage.isOfferDetailElementDisplayed());
		Assert.assertTrue("Angebot TariffProviderDescription is not Displayed ",
				tariffOfferDetailPage.isTariffProviderDescriptionDisplayed(tariffProviderDescription));
		Assert.assertTrue("FirstAvailabilityCheckButton is not Displayed ",
				tariffOfferDetailPage.isFirstAvailabilityCheckButtonDisplayed());
		Assert.assertTrue("SecondAvailabilityCheckButton is not Displayed ",
				tariffOfferDetailPage.isSecondAvailabilityCheckButtonDisplayed());
		Assert.assertTrue("TarifKostenButton is not Displayed ", tariffOfferDetailPage.isTarifKostenButtonDisplayed());
		Assert.assertTrue("Vorteile is not Displayed ", tariffOfferDetailPage.isVorteileDisplayed());
		int TarrifPriceinOfferPage = tariffOfferDetailPage.getTarrifPriceinOfferPage();
		Assert.assertTrue("TarrifPriceinOfferPage does not match with tariffProviderPrice from results page ",
				TarrifPriceinOfferPage == tariffProviderPrice);
		Assert.assertTrue("AveragePrice is not Displayed in offers page ",
				tariffOfferDetailPage.isAveragePriceDisplayed());
		Assert.assertTrue("TariffProviderDescription is not Displayed in offers page ",
				tariffOfferDetailPage.isTariffProviderDescriptionDisplayedInOffersPage(tariffProviderDescription));
		Assert.assertTrue("Optional Hardware Select is not Displayed in offers page ",
				tariffOfferDetailPage.isOptionalHardwareSelectDisplayed());
		tariffOfferDetailPage.clickHardwareSelectButton();
		Assert.assertTrue("Hardware Description is not Displayed in offers page ",
				tariffOfferDetailPage.isHardwareDescriptionDisplayed());
		logger.info(" i_should_see_the_corresponding_offer_page_for_the_selected_tariff method is passed ");
	}

	@Then("Close the browser")
	public void close_the_browser() {

		try {
			driver.close();
			driver.quit();
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(" Error while closing the browser ");

		}

	}

}
