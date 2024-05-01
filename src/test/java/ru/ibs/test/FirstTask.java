package ru.ibs.test;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class FirstTask {
    private WebDriver driver;
    private WebDriverWait wait;
    private final LocalDate date = LocalDate.now();

    @BeforeEach
    public void before() {
        System.setProperty("webdriver.edge.driver", "src/test/resources/webdriver/msedgedriver.exe");
        driver = new EdgeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().window().setSize(new Dimension(1920,1080));
        wait = new WebDriverWait(driver, Duration.ofSeconds(30, 1000));

        // Шаг 1. Переходим на страницу авторизации.
        driver.get("http://training.appline.ru/");
    }

    @Test
    public void firstTask() {
        // Производим авторизацию
        driver.findElement(By.xpath("//input[@name='_username']")).sendKeys("Taraskina Valeriya");
        driver.findElement(By.xpath("//input[@name='_password']")).sendKeys("testing");
        driver.findElement(By.xpath("//button[@name='_submit']")).click();

        // Шаг 2. Проверяем что заголовок "Панель быстрого запуска" есть на странице
        WebElement title = driver.findElement(By.xpath("//h1[@class='oro-subtitle']"));
        Assertions.assertAll(
                () -> assertTrue(title.isDisplayed(),
                        "Заголовок не отображается"),
                () -> assertEquals("Панель быстрого запуска", title.getText(),
                        "Текст заголовка не соответствует ожидаемому")
        );

        // Шаг 3. В выплывающем окне раздела Расходы нажать на Командировки
        WebElement costList = driver.findElement(By.xpath(
                "//ul[contains(@class,'main-menu')]/li/a/span[@class='title' and text()='Расходы']"));
        final String costsListElementXpath = "./ancestor::li//ul[@class='dropdown-menu menu_level_1']";

        new Actions(driver)
                .moveToElement(costList)
                .click(costList.findElement(By.xpath(costsListElementXpath)))
                .perform();

        waitPageLoader();

        // Проверяем, что мы успешно перешли на страницу "Все Командировки"
        WebElement titleBusinessTrip = driver.findElement(By.xpath("//h1[@class='oro-subtitle']"));
        Assertions.assertAll(
                () -> assertTrue(titleBusinessTrip.isDisplayed(),
                        "Заголовок не отображается"),
                () -> assertEquals("Все Командировки", titleBusinessTrip.getText(),
                        "Текст заголовка не соответствует ожидаемому")
        );

        // Шаг 4. Нажать на "Создать командировку"
        driver.findElement(By.xpath("//a[@title='Создать командировку']")).click();
        waitPageLoader();

        // Шаг 5. Проверить наличие на странице заголовка "Создать командировку"
        WebElement titleBusinessTripCreate = driver.findElement(By.xpath("//h1[@class='user-name']"));
        Assertions.assertAll(
                () -> assertTrue(titleBusinessTripCreate.isDisplayed(),
                        "Заголовок не отображается"),
                () -> assertEquals("Создать командировку", titleBusinessTripCreate.getText(),
                        "Текст заголовка не соответствует ожидаемому")
        );

        // Шаг 6. На странице создания командировки заполнить или выбрать поля:
        // Поле подразделение
        Select select = new Select(driver.findElement(By.xpath(
                "//select[@data-name='field__business-unit']")));
        select.selectByVisibleText("Отдел внутренней разработки");

        // Поле Принимающая организация
        new Actions(driver)
                .click(driver.findElement(By.xpath(
                        "//a[@id='company-selector-show']")))
                .click(driver.findElement(By.xpath(
                        "//span[@class='select2-chosen' and text()='Укажите организацию']")))
                .sendKeys("Союз тестировщиков")
                .perform();

        driver.findElement(By.xpath(
                "//span[@class='select2-match' and text()='Союз тестировщиков']")).click();

        // Кнопка заказа билетов
        WebElement checkBoxTripTaskOne = driver.findElement(By.xpath(
                "//label[text()='Заказ билетов']//..//input"));
        checkBoxTripTaskOne.click();

        // Город выбытия
        WebElement fieldDepartureCity = driver.findElement(By.xpath(
                "//input[@data-name='field__departure-city']"));
        fieldDepartureCity.clear();
        fieldDepartureCity.sendKeys("Россия, Саратов");

        // Город прибытия
        WebElement fieldArrivalCity = driver.findElement(By.xpath(
                "//input[@data-name='field__arrival-city']"));
        fieldArrivalCity.sendKeys("Россия, Калининград");

        // Планируемая дата выезда
        String departureDatePlan = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        WebElement fieldDepartureDatePlan = driver.findElement(By.xpath(
                "//input[contains(@name, 'date_selector_crm_business_trip_departureDatePlan')]"));
        fieldDepartureDatePlan.sendKeys(departureDatePlan);

        // Планируемая дата возвращения
        String returnDatePlan = date.plusDays(10).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        WebElement fieldReturnDatePlan = driver.findElement(By.xpath(
                "//input[contains(@name, 'date_selector_crm_business_trip_returnDatePlan')]"));

        // Keys.ESCAPE, чтобы корректно работал 8 шаг
        fieldReturnDatePlan.sendKeys(returnDatePlan + Keys.ESCAPE);

        // Шаг 7. Проверить, что все поля заполнены правильно
        Assertions.assertAll(
                () -> assertEquals("Отдел внутренней разработки", select.getFirstSelectedOption().getText(),
                        "Поле подразделение заполнено некорректно"),

                () -> assertEquals("Союз тестировщиков",
                        driver.findElement(By.xpath("//input[@data-name='field__company']"))
                                .getAttribute("value"),
                        "Поле с выбором организации заполнено некорректно"),

                () -> assertEquals("Россия, Саратов", fieldDepartureCity.getAttribute("value"),
                        "Поле с городом выбытия заполнено некорректно"),

                () -> assertEquals("Россия, Калининград", fieldArrivalCity.getAttribute("value"),
                        "Поле с городом прибытия заполнено некорректно"),

                () -> assertTrue(checkBoxTripTaskOne.isSelected(), "Чекбокс 'Заказ билетов' не поставлен"),

                () -> assertEquals(departureDatePlan, fieldDepartureDatePlan.getAttribute("value"),
                        "Поле с планируемой датой выезда, заполнено некорректно"),

                () -> assertEquals(returnDatePlan, fieldReturnDatePlan.getAttribute("value"),
                        "Поле с планируемой датой возвращения, заполнено некорректно")
        );

        // Шаг. 8
        driver.findElement(By.xpath(
                "//button[@class = 'btn btn-success action-button']")).click();
        waitPageLoader();

        // Шаг 9. Проверить, что на странице появилось сообщение: "Список командируемых сотрудников не может быть пустым"
        Assertions.assertTrue(driver.getPageSource().contains("Список командируемых сотрудников не может быть пустым"));
    }

    @AfterEach
    public void tearDown(){
        driver.quit();
        driver = null;
    }

    private void waitPageLoader() {
        waitUntilElementToBeInvisible(driver.findElement(By.xpath("//div[@class='loader-mask shown']")));
    }

    private Boolean waitUntilElementToBeInvisible(WebElement element) {
        return wait.until(ExpectedConditions.invisibilityOf(element));
    }
}
