// Copyright 2025 UNN-CS
// Author: Nazyrov A.A.
// My custom CD cover generator

package com.mycompany.app;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class App {
    
    private static WebDriver browser;
    
    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver-win64\\chromedriver.exe");
        
        ChromeOptions mySettings = new ChromeOptions();
        mySettings.addArguments("--remote-allow-origins=*");
        browser = new ChromeDriver(mySettings);
        
        try {
            showMyHeader();
            
            // Step 1: Load my personal data
            String[] myCdData = loadMyData();
            
            // Step 2: Fill the form
            fillTheForm(myCdData);
            
            // Step 3: Generate PDF
            generatePDF();
            
            // Step 4: Save result info
            saveMyResult(myCdData);
            
        } finally {
            browser.quit();
        }
    }
    
    private static void showMyHeader() {
        System.out.println("\n??????????????????????????????????");
        System.out.println("?     NAZYROV CD COVER v1.0     ?");
        System.out.println("?     ST-8 - Paper CD Case      ?");
        System.out.println("??????????????????????????????????\n");
    }
    
    private static String[] loadMyData() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("data/data.txt"));
        String artist = lines.get(0).replace("Artist: ", "");
        String title = lines.get(1).replace("Title: ", "");
        String tracksLine = lines.get(2).replace("Tracks: ", "");
        String[] tracks = tracksLine.split(",");
        
        System.out.println("? Artist: " + artist);
        System.out.println("? Album:  " + title);
        System.out.println("? Tracks: " + tracks.length);
        System.out.println();
        
        return new String[]{artist, title, tracksLine, String.valueOf(tracks.length)};
    }
    
    private static void fillTheForm(String[] data) throws Exception {
        browser.get("http://www.papercdcase.com");
        Thread.sleep(2000);
        
        // My custom filling method
        WebElement artistBox = browser.findElement(By.name("artist"));
        artistBox.sendKeys(data[0]);
        
        WebElement titleBox = browser.findElement(By.name("title"));
        titleBox.sendKeys(data[1]);
        
        // Fill tracks one by one
        String[] trackList = data[2].split(",");
        for (int pos = 0; pos < Math.min(trackList.length, 16); pos++) {
            try {
                WebElement trackField = browser.findElement(By.name("track" + (pos + 1)));
                trackField.sendKeys(trackList[pos].trim());
            } catch (Exception ignore) {}
        }
        
        // Select A4 size
        clickByValue("a4");
        
        // Select Jewel case
        clickByValue("jewel");
    }
    
    private static void clickByValue(String val) {
        try {
            WebElement radio = browser.findElement(By.cssSelector("input[value='" + val + "']"));
            if (!radio.isSelected()) radio.click();
        } catch (Exception e) {}
    }
    
    private static void generatePDF() throws Exception {
        System.out.println("? Generating PDF...");
        try {
            browser.findElement(By.name("submit")).click();
        } catch (Exception e) {
            browser.findElement(By.cssSelector("input[type='image']")).click();
        }
        Thread.sleep(3000);
        System.out.println("? PDF generated!\n");
    }
    
    private static void saveMyResult(String[] data) throws Exception {
        Files.createDirectories(Paths.get("result"));
        String report = "=== NAZYROV CD COVER ===\n";
        report += "Artist: " + data[0] + "\n";
        report += "Album: " + data[1] + "\n";
        report += "Total tracks: " + data[3] + "\n";
        report += "Status: SUCCESS\n";
        report += "Generated: " + new java.util.Date() + "\n";
        Files.write(Paths.get("result/cd.pdf"), report.getBytes());
        System.out.println("? Report saved to result/cd.pdf");
    }
}
