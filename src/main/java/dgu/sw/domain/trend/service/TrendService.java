package dgu.sw.domain.trend.service;

import dgu.sw.domain.trend.dto.TrendDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrendService {

    public List<TrendDTO> fetchTrends() {
        // 뉴닉 크롤링
        final String targetUrl = "https://newneek.co/@newneek/series/89";
        List<TrendDTO> trends = new ArrayList<>();

        System.setProperty("webdriver.chrome.driver", "/Users/dudtlstm/Downloads/chromedriver-mac-arm64/chromedriver");
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(targetUrl);

            // WebDriverWait 객체 생성 시 Duration 사용
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article.flex.flex-col")));

            List<WebElement> articles = driver.findElements(By.cssSelector("article.flex.flex-col"));

            for (WebElement article : articles) {
                String imageUrl = article.findElement(By.cssSelector("img")).getAttribute("src");
                String title = article.findElement(By.cssSelector("h2.break-words.text-xl.font-bold.text-gray-900")).getText();
                String content = article.findElement(By.cssSelector("p.line-clamp-2.break-all.text-gray-500")).getText();
                String author = article.findElement(By.cssSelector("strong.text-sm.font-bold.text-gray-700")).getText();
                String date = article.findElement(By.cssSelector("time")).getText();

                trends.add(TrendDTO.builder()
                        .category("뉴닉")
                        .title(title)
                        .content(content)
                        .date(date)
                        .source(author)
                        .imageUrl(imageUrl)
                        .build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return trends;
    }
}