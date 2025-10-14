package com.docker.controller;

import com.docker.entity.*;
import com.docker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitaires pour ArticleController
 * Teste la page des actualités avec les articles et leurs commentaires
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ArticleControllerTest {

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
    private List<Comment> testComments;

    @BeforeEach
    void setUp() {
        setupTestArticles();
        setupTestComments();
    }

    private void setupTestArticles() {
        testArticles = new ArrayList<>();

        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitre("Nouveau concert à Paris");
        article1.setContenu("Nous sommes ravis de vous annoncer notre prochain concert à Paris le mois prochain. Réservez vos places dès maintenant !");
        article1.setDatePublication(LocalDateTime.now().minusDays(5));
        testArticles.add(article1);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitre("Sortie de notre nouvel album");
        article2.setContenu("Notre nouvel album 'Black & White Sessions' est maintenant disponible sur toutes les plateformes de streaming.");
        article2.setDatePublication(LocalDateTime.now().minusDays(10));
        testArticles.add(article2);
    }

    private void setupTestComments() {
        testComments = new ArrayList<>();

        // Créer des utilisateurs fictifs pour les commentaires
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("jean.dupont");
        user1.setEmail("jean.dupont@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("marie.martin");
        user2.setEmail("marie.martin@example.com");

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setUser(user1);
        comment1.setContent("Super nouvelle ! J'ai hâte d'assister au concert.");
        comment1.setType(CommentType.ARTICLE);
        comment1.setTargetId(1L);
        comment1.setApproved(true);
        testComments.add(comment1);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setUser(user2);
        comment2.setContent("Votre musique est exceptionnelle !");
        comment2.setType(CommentType.ARTICLE);
        comment2.setTargetId(1L);
        comment2.setApproved(true);
        testComments.add(comment2);
    }

    // ========================================================================
    // TESTS DE LA PAGE ACTUALITÉS
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testShowNewsList_ReturnsActualitesView() throws Exception {
        when(articleService.findAllArticles()).thenReturn(testArticles);
        when(commentService.countApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(2L);
        when(commentService.getApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(testComments);

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk())
                .andExpect(view().name("actualites"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("articles", testArticles));

        verify(articleService, times(1)).findAllArticles();
        verify(commentService, times(2)).countApprovedComments(eq(CommentType.ARTICLE), anyLong());
        verify(commentService, times(2)).getApprovedComments(eq(CommentType.ARTICLE), anyLong());
    }

    @Test
    @WithAnonymousUser
    void testShowNewsList_WithEmptyArticleList() throws Exception {
        when(articleService.findAllArticles()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk())
                .andExpect(view().name("actualites"))
                .andExpect(model().attributeExists("articles"));

        verify(articleService, times(1)).findAllArticles();
        verify(commentService, never()).countApprovedComments(any(), anyLong());
        verify(commentService, never()).getApprovedComments(any(), anyLong());
    }

    @Test
    @WithAnonymousUser
    void testShowNewsList_WithMultipleArticles() throws Exception {
        // Ajouter plus d'articles
        for (int i = 3; i <= 10; i++) {
            Article article = new Article();
            article.setId((long) i);
            article.setTitre("Article " + i);
            article.setContenu("Contenu de l'article " + i + " qui est suffisamment long pour respecter la validation.");
            article.setDatePublication(LocalDateTime.now().minusDays(i));
            testArticles.add(article);
        }

        when(articleService.findAllArticles()).thenReturn(testArticles);
        when(commentService.countApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(0L);
        when(commentService.getApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk())
                .andExpect(view().name("actualites"))
                .andExpect(model().attribute("articles", testArticles));

        verify(articleService, times(1)).findAllArticles();
        verify(commentService, times(10)).countApprovedComments(eq(CommentType.ARTICLE), anyLong());
    }

    @Test
    @WithAnonymousUser
    void testShowNewsList_CommentsAreProperlyLoaded() throws Exception {
        Article singleArticle = testArticles.get(0);
        when(articleService.findAllArticles()).thenReturn(List.of(singleArticle));
        when(commentService.countApprovedComments(CommentType.ARTICLE, singleArticle.getId())).thenReturn(2L);
        when(commentService.getApprovedComments(CommentType.ARTICLE, singleArticle.getId())).thenReturn(testComments);

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk())
                .andExpect(view().name("actualites"))
                .andExpect(model().attributeExists("commentCount_1"))
                .andExpect(model().attributeExists("comments_1"));

        verify(commentService, times(1)).countApprovedComments(CommentType.ARTICLE, singleArticle.getId());
        verify(commentService, times(1)).getApprovedComments(CommentType.ARTICLE, singleArticle.getId());
    }

    @Test
    @WithAnonymousUser
    void testShowNewsList_NoCommentsForArticle() throws Exception {
        Article singleArticle = testArticles.get(0);
        when(articleService.findAllArticles()).thenReturn(List.of(singleArticle));
        when(commentService.countApprovedComments(CommentType.ARTICLE, singleArticle.getId())).thenReturn(0L);
        when(commentService.getApprovedComments(CommentType.ARTICLE, singleArticle.getId())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk())
                .andExpect(view().name("actualites"))
                .andExpect(model().attribute("commentCount_1", 0L));

        verify(commentService, times(1)).countApprovedComments(CommentType.ARTICLE, singleArticle.getId());
        verify(commentService, times(1)).getApprovedComments(CommentType.ARTICLE, singleArticle.getId());
    }

    // ========================================================================
    // TESTS AVEC AUTHENTIFICATION
    // ========================================================================

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testShowNewsList_WithAuthenticatedUser() throws Exception {
        when(articleService.findAllArticles()).thenReturn(testArticles);
        when(commentService.countApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(1L);
        when(commentService.getApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(testComments);

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk())
                .andExpect(view().name("actualites"))
                .andExpect(model().attributeExists("articles"));

        verify(articleService, times(1)).findAllArticles();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testShowNewsList_WithAdminUser() throws Exception {
        when(articleService.findAllArticles()).thenReturn(testArticles);
        when(commentService.countApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(0L);
        when(commentService.getApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk())
                .andExpect(view().name("actualites"))
                .andExpect(model().attributeExists("articles"));

        verify(articleService, times(1)).findAllArticles();
    }

    // ========================================================================
    // TESTS D'ACCESSIBILITÉ PUBLIQUE
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testActualitesPage_IsPubliclyAccessible() throws Exception {
        when(articleService.findAllArticles()).thenReturn(testArticles);
        when(commentService.countApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(0L);
        when(commentService.getApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk());
    }

    // ========================================================================
    // TESTS DE SÉCURITÉ
    // ========================================================================

    @Test
    @WithAnonymousUser
    void testActualitesPage_HasSecurityHeaders() throws Exception {
        when(articleService.findAllArticles()).thenReturn(testArticles);
        when(commentService.countApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(0L);
        when(commentService.getApprovedComments(eq(CommentType.ARTICLE), anyLong())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/actualites"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().exists("X-XSS-Protection"));
    }
}
