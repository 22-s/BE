package dgu.sw.domain.trend.service;

import dgu.sw.domain.trend.dto.TrendDTO;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrendService {

    private List<TrendDTO> cachedTrends = new ArrayList<>();
    private LocalDateTime lastFetchedTime = null;

    // 뉴닉 최신 기사 리스트 조회
    public List<TrendDTO> fetchTrends() {
        // 데이터 갱신 조건: 마지막 갱신이 없거나, 날짜가 변경된 경우
        if (lastFetchedTime == null || lastFetchedTime.toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
            cachedTrends = fetchTrendsFromWeb();
            lastFetchedTime = LocalDateTime.now();
        }
        return cachedTrends;
    }

    // 디테일 조회
    public TrendDTO fetchTrendDetail(String trendId) {
        final String targetUrl = "https://newneek.co/@newneek/article/" + trendId;

        // System.setProperty("webdriver.chrome.driver", "/Users/dudtlstm/Downloads/chromedriver-mac-arm64/chromedriver");

        // WebDriverManager로 ChromeDriver 설정
        // WebDriverManager.chromedriver().setup();

        // Chromedriver 경로 설정 (Docker 환경에서)
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

        // ChromeOptions 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        TrendDTO trendDetail = null;

        try {
            // 해당 기사 URL로 이동
            driver.get(targetUrl);

            // 페이지 로드 대기
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("article")));
            } catch (TimeoutException e) {
                // 요소를 찾지 못한 경우 예외 처리
                System.out.println("요소를 찾지 못했습니다: " + e.getMessage());
                // 크롤링 실패 로그를 기록하거나, 기본값 반환
            }

            // 데이터 크롤링
            String imageUrl = driver.findElement(By.cssSelector("div.relative img")).getAttribute("src");
            String title = driver.findElement(By.cssSelector("h1.mb-4.break-words.text-2xl.font-bold.text-gray-900")).getText();
            String content = driver.findElement(By.cssSelector("main.content")).getText();
            String author = driver.findElement(By.cssSelector("strong.line-clamp-1.text-sm.font-bold")).getText();
            String date = driver.findElement(By.cssSelector("div.flex.items-center.gap-1.text-xs.text-gray-500 time")).getText();
            String authorProfileUrl = driver.findElement(By.cssSelector("div.items-center img")).getAttribute("src");
            String category = driver.findElement(By.cssSelector("a.h-7.rounded-full.bg-gray-50")).getText();

            // DTO 생성
            trendDetail = TrendDTO.builder()
                    .id(trendId)
                    .category(category)
                    .title(title)
                    .content(content)
                    .date(date)
                    .source(author)
                    .imageUrl(imageUrl)
                    .authorProfile(authorProfileUrl)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return trendDetail;
    }


    // 최신 기사 크롤링
    private List<TrendDTO> fetchTrendsFromWeb() {
        final String targetUrl = "https://newneek.co/@newneek/series/89";
        List<TrendDTO> trends = new ArrayList<>();

        // System.setProperty("webdriver.chrome.driver", "/Users/dudtlstm/Downloads/chromedriver-mac-arm64/chromedriver");
        // WebDriver driver = new ChromeDriver();

        // WebDriverManager.chromedriver().setup();

        // Chromedriver 경로 설정 (Docker 환경에서)
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");

        // ChromeOptions 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(targetUrl);

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.block.mb-4.border-b.border-gray-100.pb-4")));
            } catch (TimeoutException e) {
                System.out.println("요소를 찾지 못했습니다: " + e.getMessage());
            }

            // 1. <a> 태그에서 href 추출 - 디테일을 위해 추출
            List<WebElement> linkElements = driver.findElements(By.cssSelector("a.block.mb-4.border-b.border-gray-100.pb-4"));
            List<String> hrefs = new ArrayList<>();
            for (WebElement link : linkElements) {
                String href = link.getAttribute("href");
                hrefs.add(href);
            }

            // 2. <article> 태그에서 나머지 정보 추출
            List<WebElement> articles = driver.findElements(By.cssSelector("article.flex.flex-col"));
            for (int i = 0; i < articles.size() && i < hrefs.size() && i < 10; i++) {
                WebElement article = articles.get(i);

                String imageUrl = article.findElement(By.cssSelector("img")).getAttribute("src");
                String title = article.findElement(By.cssSelector("h2.break-words.text-xl.font-bold.text-gray-900")).getText();
                String content = article.findElement(By.cssSelector("p.line-clamp-2.break-all.text-gray-500")).getText();
                String author = article.findElement(By.cssSelector("strong.text-sm.font-bold.text-gray-700")).getText();
                String date = article.findElement(By.cssSelector("time")).getText();

                // 3. href에서 ID 추출
                String href = hrefs.get(i);
                String id = href.substring(href.lastIndexOf("/") + 1);

                // 4. DTO 생성
                trends.add(TrendDTO.builder()
                        .id(id)
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
