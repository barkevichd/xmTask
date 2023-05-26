package tests;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.switchTo;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import java.io.File;
import java.io.FileNotFoundException;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import utils.CalendarUtils;
import utils.Period;

public class UITest {

    //in reality all that should be splitted into diff page objects
    //but for one test it make no sense
    private CalendarUtils utils = new CalendarUtils();
    private SelenideElement applyCookiesBtn = $(
            By.xpath("//button[contains(@class, 'gtm-acceptDefaultCookieFirstVisit')]"));
    private SelenideElement researchBtn = $(By.xpath("//li[@class='main_nav_research']"));
    private SelenideElement researchBtnSmall = $(
            By.xpath("//a[@aria-controls='researchMenu']//i[contains(@class, 'fa-chevron')]"));

    private SelenideElement leftMenuBtn = $(By.className("toggleLeftNav"));
    private ElementsCollection economicCalendar = $$(
            By.xpath("//a[contains(@href, 'economicCalendar')]"));
    private SelenideElement showCalendar = $(By.xpath("//span[@aria-label='Show time filter']//mat-icon"));
    private SelenideElement calendar = $(By.tagName("tc-time-filter-container"));
    private SelenideElement slider = calendar.$(By.tagName("mat-slider"));
    private SelenideElement period = calendar.$(By.xpath(".//div[@class='ng-star-inserted']"));
    private SelenideElement pastEventsBtn = $(By.xpath("//button[contains(@class, 'tc-past-events-load-button')]"));
    private SelenideElement riskWarning = $(By.xpath("//a[contains(@href, 'risk_warning')]"));
    private SelenideElement riskDisclosure = $(
            By.xpath("//div[@id='research-app']//a[contains(@href, 'XM-Risk-Disclosures')]"));

    private String scrollPosition = "{block: 'center', inline: 'nearest'}";

    @Test()
    public void uITest() throws FileNotFoundException {
        //base settings
        Configuration.browser = "chrome";
        Configuration.timeout = 10000;

        //Open Home page (make any check here if needed)
        // and apply cookies (if needed)
        Selenide.open("https://www.xm.com/");

        if (applyCookiesBtn.isDisplayed()) {
            applyCookiesBtn.click();
        }

        //check cookies screen disappears
        applyCookiesBtn.shouldNotBe(Condition.visible);

        //Click the &lt;Research and Education&gt; link located at the top menu (make any check
        //here if needed).
        if (leftMenuBtn.isDisplayed()) {
            leftMenuBtn.click();
        }
        researchBtn.shouldBe(Condition.exist);
        if (researchBtn.is(Condition.hidden)) {
            researchBtnSmall.scrollIntoView(scrollPosition).click();
        } else {
            researchBtn.scrollIntoView(scrollPosition).click();
        }

        //Click &lt;Economic Calendar&gt; link in the opened menu (make any check here if
        //needed).
        economicCalendar.findBy(Condition.visible).scrollIntoView(scrollPosition).shouldBe(Condition.visible).click();

        //switch to frame
        switchTo().frame("iFrameResizer0");
        pastEventsBtn.shouldBe(Condition.visible);
        if (showCalendar.isDisplayed()) {
            showCalendar.click();
        }
        calendar.scrollIntoView(scrollPosition).shouldBe(Condition.visible);

        //Select &lt;Today&gt; on Slider and check that the date is correct.
        utils.moveSlider(slider, period, Period.TODAY);
        utils.checkDateRangeInPicker(Period.TODAY);

        //5. Select &lt;Tomorrow&gt; on Slider and check that the date is correct.
        utils.moveSlider(slider, period, Period.TOMORROW);
        utils.checkDateRangeInPicker(Period.TOMORROW);

        //Select &lt;Next Week&gt; on Slider and check that the date is correct.
        utils.moveSlider(slider, period, Period.NEXT_WEEK);
        utils.checkDateRangeInPicker(Period.NEXT_WEEK);

        //Select &lt;Next Month&gt; on Slider and check that the date is correct.
        utils.moveSlider(slider, period, Period.NEXT_MONTH);
        utils.checkDateRangeInPicker(Period.NEXT_MONTH);

        //switch back to top
        switchTo().defaultContent();

        //Click &lt;here&gt; link in the “disclaimer” block at the bottom (make any check here if
        //needed).
        riskWarning.scrollIntoView(scrollPosition).click();

        //Click &lt;here&gt; link in the “Risk Warning” block at the bottom.
        //Check that &lt;Risk Disclosure&gt; document was opened in new tab.

        //Not sure what exact verification needed here
        //just checked that file downloaded and not empty
        //can be also checked with expected condition windows count to be
        File f = riskDisclosure.download();
        Assert.assertTrue(f.length() > 0, "Downloaded file is empty");
    }
}
