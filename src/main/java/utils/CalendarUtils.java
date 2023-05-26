package utils;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.Assert;

public class CalendarUtils {

    private ElementsCollection nextBtn = $$(By.xpath("//button[@aria-label='Next month']"));
    private ElementsCollection previousBtn = $$(By.xpath("//button[@aria-label='Previous month']"));
    private String format = "EEE MMM dd yyyy";

    //most likely it is possible to move slider using js but I dont know the correct method to call it
    //so did it using arrows
    public void moveSlider(SelenideElement slider, SelenideElement period, Period requiredPeriod) {
        boolean isSelected = false;
        int minVal = Integer.parseInt(slider.getAttribute("aria-valuemin"));
        int maxVal = Integer.parseInt(slider.getAttribute("aria-valuemax"));
        int current = Integer.parseInt(slider.getAttribute("aria-valuenow"));
        if (current > 0) {
            for (int i = current; i > 0; i--) {
                slider.sendKeys(Keys.ARROW_LEFT);
            }
        }

        for (int i = minVal; i <= maxVal; i++) {
            slider.sendKeys(Keys.ARROW_RIGHT);
            slider.shouldHave(Condition.attribute("aria-valuenow", "" + (i + 1) + ""));
            if (period.getText().equalsIgnoreCase(requiredPeriod.getValue())) {
                isSelected = true;
                break;
            }
        }
        Assert.assertTrue(isSelected, "Could not select required period using slider");
    }

    public void checkDateRangeInPicker(Period requiredPeriod) {
        ElementsCollection dates = $$(By.xpath("//div[contains(@class, 'mat-calendar-body-selected')]"));
        Calendar calendar = Calendar.getInstance();
        switch (requiredPeriod) {
            case TODAY:
                dates.filterBy(Condition.visible).shouldHave(CollectionCondition.size(1)).get(0)
                        .parent().shouldHave(Condition.attribute("aria-label",
                                new SimpleDateFormat(format).format(calendar.getTime())));
                break;
            case TOMORROW:
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                boolean navigated = navigateNext(dates, 0);
                dates.filterBy(Condition.visible).shouldHave(CollectionCondition.size(1)).get(0)
                        .parent().shouldHave(Condition.attribute("aria-label",
                                new SimpleDateFormat(format).format(calendar.getTime())));
                if (navigated) {
                    previousBtn.findBy(Condition.visible).click();
                }
                break;
            case NEXT_WEEK:
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int monday = (9 - dayOfWeek) % 7;
                navigateAndCheck(calendar, dates, monday, 6, false);
                break;
            case NEXT_MONTH:
                if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
                    calendar.set(Calendar.MONTH, Calendar.JANUARY);
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
                } else {
                    calendar.roll(Calendar.MONTH, true);
                }
                navigateAndCheck(calendar, dates, 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), true);
                break;
            default:
                throw new CustomException("there is no such period");
        }
    }

    private void navigateAndCheck(Calendar calendar, ElementsCollection dates, int firstDay, int lastDay, boolean set) {
        if (set) {
            calendar.set(Calendar.DATE, firstDay);
        } else {
            calendar.add(Calendar.DATE, firstDay);
        }
        boolean navigated = navigateNext(dates, 0);
        dates.filterBy(Condition.visible).shouldHave(CollectionCondition.sizeGreaterThan(0)).first()
                .parent().shouldHave(Condition.attribute("aria-label",
                        new SimpleDateFormat(format).format(calendar.getTime())));
        if (!navigated) {
            navigated = navigateNext(dates, 1);
        }
        if (set) {
            calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, lastDay);
        }
        dates.filterBy(Condition.visible).shouldHave(CollectionCondition.sizeGreaterThan(0)).last()
                .parent().shouldHave(Condition.attribute("aria-label",
                        new SimpleDateFormat(format).format(calendar.getTime())));
        if (navigated) {
            previousBtn.findBy(Condition.visible).click();
        }
    }

    private boolean navigateNext(ElementsCollection dates, int count) {
        if (dates.filterBy(Condition.visible).size() <= count) {
            nextBtn.findBy(Condition.visible).click();
            return true;
        }
        return false;
    }
}
