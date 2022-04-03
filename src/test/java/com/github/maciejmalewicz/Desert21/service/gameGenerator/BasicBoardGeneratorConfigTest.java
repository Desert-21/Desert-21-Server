package com.github.maciejmalewicz.Desert21.service.gameGenerator;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SnapshotExtension.class})
class BasicBoardGeneratorConfigTest {

    private Expect expect;

    @SnapshotName("basic_board_generator")
    @Test
    public void shouldMatchSnapshot() {
        var basicBoardGenerator = new BasicBoardGeneratorConfig();
        var serializer = new JacksonSerializer<BasicBoardGeneratorConfig>();
        var serialized = new String(serializer.serialize(basicBoardGenerator));
        expect.toMatchSnapshot(serialized);
    }

}