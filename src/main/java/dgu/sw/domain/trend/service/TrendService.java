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
//    public TrendDTO fetchTrendDetail(String trendId) {
//        // 캐시된 리스트에서 ID로 데이터 조회
//        return cachedTrends.stream()
//                .filter(trend -> trend.getTitle().hashCode() == trendId.hashCode()) // 제목 기반의 해시 값으로 비교
//                .findFirst()
//                .orElse(null);
//    }

    // 최신 기사 크롤링
    private List<TrendDTO> fetchTrendsFromWeb() {
        final String targetUrl = "https://newneek.co/@newneek/series/89";
        List<TrendDTO> trends = new ArrayList<>();

        System.setProperty("webdriver.chrome.driver", "/Users/dudtlstm/Downloads/chromedriver-mac-arm64/chromedriver");
        WebDriver driver = new ChromeDriver();

        try {
            driver.get(targetUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.block.mb-4.border-b.border-gray-100.pb-4")));

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
