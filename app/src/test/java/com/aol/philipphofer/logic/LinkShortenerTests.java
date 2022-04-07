package com.aol.philipphofer.logic;

import com.aol.philipphofer.logic.help.LinkShortener;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LinkShortenerTests {

    @Test
    public void testGeneration() {
        String link = "02303049923333234322031030200000003040230420402302012303023";
        String shortened = LinkShortener.getLink(link);

        assertEquals(link, LinkShortener.getId(shortened));
    }
}
