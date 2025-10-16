package com.docker.service;

import com.docker.entity.SocialLink;
import com.docker.repository.SocialLinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialLinkServiceTest {

    @Mock
    private SocialLinkRepository socialLinkRepository;

    @InjectMocks
    private SocialLinkService socialLinkService;

    private SocialLink testLink1;
    private SocialLink testLink2;

    @BeforeEach
    void setUp() {
        testLink1 = new SocialLink();
        testLink1.setId(1L);
        testLink1.setName("Facebook");
        testLink1.setUrl("https://facebook.com/test");
        testLink1.setEnabled(true);
        testLink1.setDisplayOrder(1);

        testLink2 = new SocialLink();
        testLink2.setId(2L);
        testLink2.setName("Twitter");
        testLink2.setUrl("https://twitter.com/test");
        testLink2.setEnabled(false);
        testLink2.setDisplayOrder(2);
    }

    @Test
    void testGetEnabledSocialLinks_ReturnsOnlyEnabledLinks() {
        List<SocialLink> enabledLinks = Arrays.asList(testLink1);
        when(socialLinkRepository.findByEnabledTrueOrderByDisplayOrderAsc()).thenReturn(enabledLinks);

        List<SocialLink> result = socialLinkService.getEnabledSocialLinks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getEnabled());
        verify(socialLinkRepository, times(1)).findByEnabledTrueOrderByDisplayOrderAsc();
    }

    @Test
    void testGetAllSocialLinks_ReturnsAllLinks() {
        List<SocialLink> allLinks = Arrays.asList(testLink1, testLink2);
        when(socialLinkRepository.findAllByOrderByDisplayOrderAsc()).thenReturn(allLinks);

        List<SocialLink> result = socialLinkService.getAllSocialLinks();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(socialLinkRepository, times(1)).findAllByOrderByDisplayOrderAsc();
    }

    @Test
    void testGetSocialLinkById_WhenExists_ReturnsLink() {
        when(socialLinkRepository.findById(1L)).thenReturn(Optional.of(testLink1));

        SocialLink result = socialLinkService.getSocialLinkById(1L);

        assertNotNull(result);
        assertEquals("Facebook", result.getName());
        verify(socialLinkRepository, times(1)).findById(1L);
    }

    @Test
    void testGetSocialLinkById_WhenNotExists_ThrowsException() {
        when(socialLinkRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            socialLinkService.getSocialLinkById(999L);
        });

        verify(socialLinkRepository, times(1)).findById(999L);
    }

    @Test
    void testSaveSocialLink_WithValidUrl_SavesLink() {
        when(socialLinkRepository.save(any(SocialLink.class))).thenReturn(testLink1);

        SocialLink result = socialLinkService.saveSocialLink(testLink1);

        assertNotNull(result);
        verify(socialLinkRepository, times(1)).save(testLink1);
    }

    @Test
    void testSaveSocialLink_WithEmptyUrl_DisablesLink() {
        testLink1.setUrl("");
        when(socialLinkRepository.save(any(SocialLink.class))).thenReturn(testLink1);

        SocialLink result = socialLinkService.saveSocialLink(testLink1);

        assertFalse(result.getEnabled());
        verify(socialLinkRepository, times(1)).save(testLink1);
    }

    @Test
    void testSaveSocialLink_WithNullUrl_DisablesLink() {
        testLink1.setUrl(null);
        when(socialLinkRepository.save(any(SocialLink.class))).thenReturn(testLink1);

        SocialLink result = socialLinkService.saveSocialLink(testLink1);

        assertFalse(result.getEnabled());
        verify(socialLinkRepository, times(1)).save(testLink1);
    }

    @Test
    void testUpdateSocialLink_WithValidUrl_UpdatesLink() {
        when(socialLinkRepository.findById(1L)).thenReturn(Optional.of(testLink1));
        when(socialLinkRepository.save(any(SocialLink.class))).thenReturn(testLink1);

        socialLinkService.updateSocialLink(1L, "https://facebook.com/newurl", true);

        assertEquals("https://facebook.com/newurl", testLink1.getUrl());
        assertTrue(testLink1.getEnabled());
        verify(socialLinkRepository, times(1)).findById(1L);
        verify(socialLinkRepository, times(1)).save(testLink1);
    }

    @Test
    void testUpdateSocialLink_WithEmptyUrl_DisablesLink() {
        when(socialLinkRepository.findById(1L)).thenReturn(Optional.of(testLink1));
        when(socialLinkRepository.save(any(SocialLink.class))).thenReturn(testLink1);

        socialLinkService.updateSocialLink(1L, "", true);

        assertFalse(testLink1.getEnabled());
        verify(socialLinkRepository, times(1)).save(testLink1);
    }

    @Test
    void testUpdateSocialLink_WithNullEnabled_DefaultsToTrue() {
        when(socialLinkRepository.findById(1L)).thenReturn(Optional.of(testLink1));
        when(socialLinkRepository.save(any(SocialLink.class))).thenReturn(testLink1);

        socialLinkService.updateSocialLink(1L, "https://facebook.com/test", null);

        assertTrue(testLink1.getEnabled());
        verify(socialLinkRepository, times(1)).save(testLink1);
    }

    @Test
    void testDeleteSocialLink_DeletesLink() {
        doNothing().when(socialLinkRepository).deleteById(1L);

        socialLinkService.deleteSocialLink(1L);

        verify(socialLinkRepository, times(1)).deleteById(1L);
    }
}
