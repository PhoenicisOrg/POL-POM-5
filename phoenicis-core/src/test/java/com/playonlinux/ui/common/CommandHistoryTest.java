/*
 * Copyright (C) 2015 Markus Ebner
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.playonlinux.ui.common;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

public class CommandHistoryTest {

    private final CommandHistory history = new CommandHistory();

    private CommandHistory.Item[] testItems = new CommandHistory.Item[2];

    @Before
    public void setUp() {
        testItems[0] = new CommandHistory.Item("item0", 3);
        testItems[1] = new CommandHistory.Item("item1", 4);
    }

    @Test
    public void testHistory() {
        //EMPTY history should always return an EMPTY item.
        assertSame(CommandHistory.Item.EMPTY, history.current());
        history.up();
        history.up();
        assertSame(CommandHistory.Item.EMPTY, history.current());
        history.down();
        assertSame(CommandHistory.Item.EMPTY, history.current());

        history.add(testItems[0]);
        //history should be reset to point to an EMPTY item after adding a new item
        assertSame(CommandHistory.Item.EMPTY, history.current());
        //after going one up, history should be at the latest added item.
        assertSame(testItems[0], history.up());
        assertSame(testItems[0], history.current());
        //calling up even though the history is already pointing to the oldest item should
        //have no effect => still pointing to the oldest item
        assertSame(testItems[0], history.up());
        assertSame(testItems[0], history.current());
        //going back down should result in the history pointing to an EMPTY item
        assertSame(CommandHistory.Item.EMPTY, history.down());
        assertSame(CommandHistory.Item.EMPTY, history.current());

        history.add(testItems[1]);
        //history should always point to an EMPTY item after adding a new item
        assertSame(CommandHistory.Item.EMPTY, history.current());
        //after going one up, history should be at the latest added item.
        assertSame(testItems[1], history.up());
        assertSame(testItems[1], history.current());
        //after going up once more, history should point at the second latest added item.
        assertSame(testItems[0], history.up());
        assertSame(testItems[0], history.current());
        //going back down, history should again point to the latest added item
        assertSame(testItems[1], history.down());
        assertSame(testItems[1], history.current());
    }

}
