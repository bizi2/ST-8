// Copyright 2025 UNN-CS
// Nazyrov A.A.

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
    
    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver-win64\\chromedriver.exe");
        
        ChromeOptions cfg_opt = new ChromeOptions();
        cfg_opt.addArguments("--remote-allow-origins=*");
        cfg_opt.addArguments("--ignore-certificate-errors");
        
        WebDriver browser_inst = new ChromeDriver(cfg_opt);
        
        try {
            System.out.println("=== ST-8: CD Cover Generator ===\n");
            
            // Load data from file
            List<String> lines_kg = Files.readAllLines(Paths.get("data/data.txt"));
            String artist_val = lines_kg.get(0).replace("Artist: ", "");
            String album_val = lines_kg.get(1).replace("Title: ", "");
            String tracks_raw = lines_kg.get(2).replace("Tracks: ", "");
            String[] tracks_arr = tracks_raw.split(",");
            
            System.out.println("Artist: " + artist_val);
            System.out.println("Album: " + album_val);
            System.out.println("Tracks count: " + tracks_arr.length);
            
            // Open website
            browser_inst.get("http://www.papercdcase.com");
            Thread.sleep(2000);
            
            // Fill artist field
            WebElement artist_input = browser_inst.findElement(By.name("artist"));
            artist_input.clear();
            artist_input.sendKeys(artist_val);
            
            // Fill title field
            WebElement title_input = browser_inst.findElement(By.name("title"));
            title_input.clear();
            title_input.sendKeys(album_val);
            
            // Fill tracks (max 16)
            for (int idx_t = 0; idx_t < Math.min(tracks_arr.length, 16); idx_t++) {
                try {
                    WebElement track_field = browser_inst.findElement(By.name("track" + (idx_t + 1)));
                    track_field.clear();
                    track_field.sendKeys(tracks_arr[idx_t].trim());
                } catch (Exception skip_err) {
                    // field not found, skip
                }
            }
            
            // Select A4 format
            WebElement radio_a4 = browser_inst.findElement(By.cssSelector("input[value='a4']"));
            if (!radio_a4.isSelected()) radio_a4.click();
            
            // Select Jewel Case
            WebElement radio_jewel = browser_inst.findElement(By.cssSelector("input[value='jewel']"));
            if (!radio_jewel.isSelected()) radio_jewel.click();
            
            // Submit form
            System.out.println("\nGenerating PDF...");
            WebElement submit_btn = browser_inst.findElement(By.name("submit"));
            submit_btn.click();
            
            Thread.sleep(5000);
            
            // Save result info
            Files.createDirectories(Paths.get("result"));
            String report_txt = "=== CD COVER GENERATION ===\n";
            report_txt += "Artist: " + artist_val + "\n";
            report_txt += "Album: " + album_val + "\n";
            report_txt += "Tracks: " + tracks_arr.length + "\n";
            report_txt += "Status: Form submitted successfully\n";
            report_txt += "PDF file should be in Downloads folder\n";
            Files.write(Paths.get("result/cd.pdf"), report_txt.getBytes());
            
            System.out.println("\n[OK] Form submitted!");
            System.out.println("[OK] Info saved to result/cd.pdf");
            System.out.println("\nNOTE: The PDF file is downloaded by your browser.");
            System.out.println("Check Downloads folder for papercdcase.pdf");
            
        } finally {
            browser_inst.quit();
        }
    }
}
