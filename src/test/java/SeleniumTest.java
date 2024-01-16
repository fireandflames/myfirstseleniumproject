import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeleniumTest {
    final private CharSequence gmailUser = "t1e2s3t4vhselenium@gmail.com";
    final private CharSequence outlookUser = "t1e2s3t4vhselenium@outlook.com";
    final private CharSequence validPassword = "Seleniumpassword123";
    final private WebDriver driver = new ChromeDriver();
    final private CharSequence subject = "mySubject";
    final private CharSequence message = "Hello world!";

    void enterLoginAndPasswordGmail(WebDriver driver, CharSequence user, CharSequence password) throws InterruptedException {
        driver.get("https://www.google.com/gmail/about/");
        driver.findElement(By.xpath("//*[contains(text(),'Sign in')]")).click();
        driver.findElement(By.name("identifier")).sendKeys(user);
        driver.findElement(By.id("identifierNext")).click();
        Thread.sleep(7000);
        driver.findElement(By.name("Passwd")).sendKeys(password);
        driver.findElement(By.id("passwordNext")).click();
        Thread.sleep(3000);
    }

    public void enterLoginAndPasswordOutlook(WebDriver driver, CharSequence user, CharSequence password) throws InterruptedException {
        driver.get("https://outlook.live.com");
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("[aria-label='Sign in to your account']")).click();
        Thread.sleep(3000);
        driver.findElement(By.cssSelector("[aria-label='Enter your email, phone, or Skype.']")).sendKeys(user);
        driver.findElement(By.cssSelector("[aria-label='Enter your email, phone, or Skype.']")).sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        driver.findElement(By.name("passwd")).sendKeys(password);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id='idSIButton9']")).click();
        Thread.sleep(500);
        if(!driver.findElements(By.xpath("//*[@id='idBtn_Back']")).isEmpty())
        {
            driver.findElement(By.xpath("//*[@id='idBtn_Back']")).click();
        }
    }

    @Test
    @Order(1)
    public void loginSuccessfulGmail() throws InterruptedException {
        enterLoginAndPasswordGmail(driver, gmailUser, validPassword);
        assertEquals(driver.getCurrentUrl(), "https://mail.google.com/mail/u/0/#inbox");
        driver.close();

    }

    @Test
    @Order(2)
    public void loginSuccessfulOutlook() throws InterruptedException {
        enterLoginAndPasswordOutlook(driver, outlookUser, validPassword);
        assertEquals(driver.getCurrentUrl(),"https://outlook.live.com/mail/0/");
        driver.close();
    }

    @Test
    @Order(3)
    public void loginUnsuccessfulPasswordGmail() throws InterruptedException {
        enterLoginAndPasswordGmail(driver, gmailUser, "fakepassword");
        assertNotEquals(driver.getCurrentUrl(), "https://mail.google.com/mail/u/0/#inbox");
        driver.close();
    }

    @Test
    @Order(5)
    public void loginUnsuccessfulPasswordAndLoginGmail() throws InterruptedException {
        //messes up when google decides that browser not secure
        enterLoginAndPasswordGmail(driver, "testuser@gmail.com", "fakepassword123");
        assertNotEquals(driver.getCurrentUrl(), "https://mail.google.com/mail/u/0/#inbox");
        driver.close();
    }

    @Test
    @Order(4)
    public void loginUnsuccessfulPasswordOutlook() throws InterruptedException {
        enterLoginAndPasswordOutlook(driver, outlookUser, "fakepassword");
        assertNotEquals(driver.findElements(By.className("error")).size(), 0);
        driver.close();
    }

    @Test
    @Order(6)
    public void loginUnsuccessfulPasswordAndLoginOutlook() throws InterruptedException {
        enterLoginAndPasswordOutlook(driver, "fakeuser@mail.com", "fakepassword");
        assertNotEquals(driver.findElements(By.id("usernameError")).size(), 0);
        driver.close();
    }
    @Test
    @Order(7)
    public void loginUnsuccessfulEmptyGmail()  {
        assertThrowsExactly(org.openqa.selenium.NoSuchElementException.class, () -> enterLoginAndPasswordGmail(driver, "", ""));
        driver.close();
    }

    @Test
    @Order(8)
    public void loginUnsuccessfulEmptyOutlook() throws InterruptedException {
        enterLoginAndPasswordOutlook(driver, "", "");
        assertNotEquals(driver.findElements(By.className("error")).size(), 0);
        driver.close();
    }

    @Test
    @Order(9)
    public void sendFromGmailToOutlook() throws InterruptedException {
        enterLoginAndPasswordGmail(driver, gmailUser, validPassword);
        driver.findElement(By.className("aic")).click();
        Thread.sleep(2000);
        driver.findElement(By.cssSelector("[aria-label='To recipients']")).sendKeys(outlookUser);
        driver.findElement(By.cssSelector("[aria-label='Subject']")).sendKeys(subject);
        Thread.sleep(1000);
        driver.findElement(By.xpath("/html/body/div[22]/div/div/div/div[1]/div[2]/div[1]/div[1]" +
                "/div/div/div/div[3]/div/div/div[4]/table/tbody/tr/td[2]/table/tbody/tr[1]/td/div/div[1]/div[2]" +
                "/div[3]/div/table/tbody/tr/td[2]/div[2]/div")).sendKeys(message);
        Thread.sleep(2000);
        driver.findElement(By.id(":7u")).click();
        Thread.sleep(500);
        driver.close();
   }

   @Test
   @Order(10)
   public void checkOutlookForMail() throws InterruptedException {
       driver.close();
       WebDriver secondDriver = new ChromeDriver();
       enterLoginAndPasswordOutlook(secondDriver, outlookUser, validPassword);
       Thread.sleep(5000);
       secondDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/div[2]/div[2]/div[1]/div/div/" +
               "div[3]/div/div/div[1]/div[2]/div/div/div/div/div/div/div/div[2]/div/div/div/div/div[2]")).click();
       Thread.sleep(2000);
       WebElement email = secondDriver.findElement(By.cssSelector("[aria-label='Email message']"));
       WebElement readButton = secondDriver.findElement(By.xpath("/html/body/div[1]/div/div[2]/div/div[2]/" +
               "div[2]/div[1]/div/div/div[3]/div/div/div[1]/div[2]/div/div/div/div/div/div/div/div[2]/div/" +
               "div/div/div/div[1]/button"));

       assertDoesNotThrow(()-> {if(!(email.getText().contains("Test Selenium") &&
               email.getText().contains(gmailUser) &&
               email.getText().contains(message) &&
               readButton.getAttribute("title").equals("Mark as read"))) {throw new Exception();}});

   }

}
