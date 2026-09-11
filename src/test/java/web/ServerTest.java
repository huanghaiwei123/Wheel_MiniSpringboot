package web;

import org.junit.jupiter.api.Test;
import web.core.ClassScanner;

public class ServerTest {
    @Test
    public void classScanTest() {
        ClassScanner.scan("web").forEach(c->System.out.println(c.getName()));
    }
}
