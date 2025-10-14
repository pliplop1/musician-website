package com.docker.controller;

import com.docker.entity.Article;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour AdminArticleController
 * Teste la gestion admin des articles (affichage, création, modification, suppression)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private SocialLinkService socialLinkService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    private List<Article> testArticles;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitre("Premier article de test");
        article1.setContenu("Ceci est le contenu du premier article de test avec suffisamment de caractères.");
        article1.setDatePublication(LocalDateTime.now());

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitre("Deuxième article de test");
        article2.setContenu("Ceci est le contenu du deuxième article de test avec suffisamment de caractères.");
        article2.setDatePublication(LocalDateTime.now());

        testArticles = Arrays.asList(article1, article2);
        testArticle = article1;
    }

    // ========================================================================
    // TESTS AFFICHAGE PAGE GESTION ARTICLES
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowArticleManagementPage_AsAdmin_ReturnsView() throws Exception {
        when(articleService.findAllArticles()).thenReturn(testArticles);

        mockMvc.perform(get("/admin/articles"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/article-management"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("articles", testArticles));

        verify(articleService, times(1)).findAllArticles();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowArticleManagementPage_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/articles"))
                .andExpect(status().isForbidden());

        verify(articleService, never()).findAllArticles();
    }

    // ========================================================================
    // TESTS AJOUT ARTICLE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveArticle_AsAdmin_Success() throws Exception {
        when(articleService.saveArticle(any(Article.class))).thenReturn(testArticle);

        mockMvc.perform(post("/admin/articles/add")
                .param("titre", "Nouvel article test")
                .param("contenu", "Contenu valide avec au moins 20 caractères pour respecter la validation.")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(articleService, times(1)).saveArticle(any(Article.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testSaveArticle_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/articles/add")
                .param("titre", "Nouvel article test")
                .param("contenu", "Contenu valide avec au moins 20 caractères pour respecter la validation.")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(articleService, never()).saveArticle(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testSaveArticle_ValidationErrors_ReturnsFormWithErrors() throws Exception {
        when(articleService.findAllArticles()).thenReturn(testArticles);

        // Titre trop court (moins de 5 caractères) et contenu trop court (moins de 20 caractères)
        mockMvc.perform(post("/admin/articles/add")
                .param("titre", "Test")  // Trop court
                .param("contenu", "Court")  // Trop court
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/article-management"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeHasFieldErrors("article", "titre", "contenu"));

        verify(articleService, never()).saveArticle(any());
        verify(articleService, times(1)).findAllArticles();
    }

    // ========================================================================
    // TESTS SUPPRESSION ARTICLE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteArticle_AsAdmin_Success() throws Exception {
        doNothing().when(articleService).deleteArticle(1L);

        mockMvc.perform(post("/admin/articles/delete/1")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(articleService, times(1)).deleteArticle(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteArticle_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/articles/delete/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(articleService, never()).deleteArticle(any());
    }

    // ========================================================================
    // TESTS AFFICHAGE FORMULAIRE EDITION ARTICLE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowEditArticleForm_AsAdmin_Success() throws Exception {
        when(articleService.findArticleById(1L)).thenReturn(Optional.of(testArticle));

        mockMvc.perform(get("/admin/articles/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-article"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("article", testArticle));

        verify(articleService, times(1)).findArticleById(1L);
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testShowEditArticleForm_AsUser_Returns403() throws Exception {
        mockMvc.perform(get("/admin/articles/edit/1"))
                .andExpect(status().isForbidden());

        verify(articleService, never()).findArticleById(any());
    }

    // ========================================================================
    // TESTS MODIFICATION ARTICLE
    // ========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateArticle_AsAdmin_Success() throws Exception {
        when(articleService.findArticleById(1L)).thenReturn(Optional.of(testArticle));
        when(articleService.saveArticle(any(Article.class))).thenReturn(testArticle);

        mockMvc.perform(post("/admin/articles/update/1")
                .param("titre", "Article modifié avec titre valide")
                .param("contenu", "Contenu modifié avec au moins 20 caractères pour respecter la validation.")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(articleService, times(1)).findArticleById(1L);
        verify(articleService, times(1)).saveArticle(any(Article.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateArticle_AsUser_Returns403() throws Exception {
        mockMvc.perform(post("/admin/articles/update/1")
                .param("titre", "Article modifié avec titre valide")
                .param("contenu", "Contenu modifié avec au moins 20 caractères pour respecter la validation.")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(articleService, never()).findArticleById(any());
        verify(articleService, never()).saveArticle(any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateArticle_ValidationErrors_ReturnsFormWithErrors() throws Exception {
        // Validation échoue avant l'appel au service
        mockMvc.perform(post("/admin/articles/update/1")
                .param("titre", "Test")  // Trop court
                .param("contenu", "Court")  // Trop court
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-article"))
                .andExpect(model().attributeHasFieldErrors("article", "titre", "contenu"));

        verify(articleService, never()).findArticleById(any());
        verify(articleService, never()).saveArticle(any());
    }
}
