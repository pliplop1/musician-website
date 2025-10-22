package com.docker.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Génération du sitemap.xml pour le SEO
 */
@Controller
public class SitemapController {

    private static final String BASE_URL = "https://duoblackandwhite.com";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemap() {
        String today = LocalDate.now().format(DATE_FORMATTER);

        StringBuilder sitemap = new StringBuilder();
        sitemap.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sitemap.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Page d'accueil
        addUrl(sitemap, BASE_URL + "/", "1.0", "daily", today);

        // Pages principales
        addUrl(sitemap, BASE_URL + "/biographie", "0.9", "weekly", today);
        addUrl(sitemap, BASE_URL + "/galerie", "0.8", "weekly", today);
        addUrl(sitemap, BASE_URL + "/musique", "0.9", "weekly", today);
        addUrl(sitemap, BASE_URL + "/videos", "0.8", "weekly", today);
        addUrl(sitemap, BASE_URL + "/actualites", "0.7", "daily", today);

        // Pages légales
        addUrl(sitemap, BASE_URL + "/privacy-policy", "0.3", "monthly", today);
        addUrl(sitemap, BASE_URL + "/mentions-legales", "0.3", "monthly", today);
        addUrl(sitemap, BASE_URL + "/cookies", "0.3", "monthly", today);

        // Auth pages (lower priority)
        addUrl(sitemap, BASE_URL + "/login", "0.2", "monthly", today);
        addUrl(sitemap, BASE_URL + "/register", "0.2", "monthly", today);

        sitemap.append("</urlset>");

        return sitemap.toString();
    }

    private void addUrl(StringBuilder sitemap, String loc, String priority, String changefreq, String lastmod) {
        sitemap.append("  <url>\n");
        sitemap.append("    <loc>").append(loc).append("</loc>\n");
        sitemap.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
        sitemap.append("    <changefreq>").append(changefreq).append("</changefreq>\n");
        sitemap.append("    <priority>").append(priority).append("</priority>\n");
        sitemap.append("  </url>\n");
    }
}
