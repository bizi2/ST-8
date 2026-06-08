// Copyright 2025 UNN-CS
// Nazyrov A.A.

package com.nazyrov.st8;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    
    public static void main(String[] args) throws Exception {
        // Setup download folder
        String downloadPath = Paths.get("result").toAbsolutePath().toString();
        
        ChromeOptions opt = new ChromeOptions();
        opt.addArguments("--remote-allow-origins=*");
        opt.addArguments("--ignore-certificate-errors");
        
        // Configure auto-download
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", downloadPath);
        prefs.put("download.prompt_for_download", false);
        prefs.put("plugins.always_open_pdf_externally", true);
        opt.setExperimentalOption("prefs", prefs);
        
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver(opt);
        
        try {
            System.out.println("+------------------------------------------+");
            System.out.println("|     NAZYROV CD COVER GENERATOR v2.0     |");
            System.out.println("|     ST-8 - Selenium Automation          |");
            System.out.println("+------------------------------------------+\n");
            
            // ===== ??????? 2: ???????? ?????? ?? data.txt =====
            List<String> fileLines = Files.readAllLines(Paths.get("data/data.txt"));
            String singer = fileLines.get(0).replace("Artist: ", "");
            String albumTitle = fileLines.get(1).replace("Title: ", "");
            String tracksRaw = fileLines.get(2).replace("Tracks: ", "");
            String[] tracksArray = tracksRaw.split(",");
            
            System.out.println("[DATA] Artist: " + singer);
            System.out.println("[DATA] Album:  " + albumTitle);
            System.out.println("[DATA] Tracks: " + tracksArray.length);
            System.out.println();
            
            // ===== ??????? 3: ?????? ? ?????? =====
            System.out.println("[1] Opening papercdcase.com...");
            driver.get("http://www.papercdcase.com");
            Thread.sleep(2000);
            
            // ????????? Artist
            WebElement artistBox = driver.findElement(By.name("artist"));
            artistBox.clear();
            artistBox.sendKeys(singer);
            System.out.println("[2] Artist filled: " + singer);
            
            // ????????? Title
            WebElement titleBox = driver.findElement(By.name("title"));
            titleBox.clear();
            titleBox.sendKeys(albumTitle);
            System.out.println("[3] Title filled: " + albumTitle);
            
            // ????????? ????? (?? 16)
            int maxTracks = Math.min(tracksArray.length, 16);
            for (int idx = 0; idx < maxTracks; idx++) {
                try {
                    WebElement trackBox = driver.findElement(By.name("track" + (idx + 1)));
                    trackBox.clear();
                    trackBox.sendKeys(tracksArray[idx].trim());
                } catch (Exception e) {
                    System.out.println("[WARN] Track " + (idx+1) + " field not found");
                }
            }
            System.out.println("[4] Tracks filled: " + maxTracks);
            
            // ???????? ?????? A4
            WebElement radioA4 = driver.findElement(By.cssSelector("input[value='a4']"));
            if (!radioA4.isSelected()) radioA4.click();
            System.out.println("[5] Format A4 selected");
            
            // ???????? Jewel Case
            WebElement radioJewel = driver.findElement(By.cssSelector("input[value='jewel']"));
            if (!radioJewel.isSelected()) radioJewel.click();
            System.out.println("[6] Jewel Case selected");
            
            // ?????????? ?????
            System.out.println("[7] Submitting form...");
            WebElement submitBtn = driver.findElement(By.name("submit"));
            submitBtn.click();
            
            // ???? ????????? PDF
            Thread.sleep(5000);
            System.out.println("[8] PDF generation requested");
            
            // ?????????, ???????? ?? PDF
            java.nio.file.Path pdfFile = Paths.get(downloadPath, "papercdcase.pdf");
            if (Files.exists(pdfFile)) {
                System.out.println("[9] PDF found! Size: " + Files.size(pdfFile) + " bytes");
                // ???????? ? cd.pdf
                Files.copy(pdfFile, Paths.get(downloadPath, "cd.pdf"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[10] Saved as result/cd.pdf");
            } else {
                System.out.println("[WARN] PDF not found in Downloads, check manually");
                // ??????? ?????????????? ????
                String info = "PDF should be in: " + downloadPath + "\n";
                info += "Please manually rename papercdcase.pdf to cd.pdf";
                Files.write(Paths.get("result/cd.pdf"), info.getBytes());
            }
            
            System.out.println("\n+------------------------------------------+");
            System.out.println("|     ALL TASKS COMPLETED!                |");
            System.out.println("+------------------------------------------+");
            
        } finally {
            driver.quit();
        }
    }
}
