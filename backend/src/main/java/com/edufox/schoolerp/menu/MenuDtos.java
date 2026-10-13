package com.edufox.schoolerp.menu;

import java.util.List;

public final class MenuDtos {
    private MenuDtos() {}
    public record MenuResponse(String schoolName, String session, List<Module> modules) {}
    public record Module(int moduleId, String id, String label, String icon, String redirectUrl, List<Group> groups) {}
    public record Group(String id, String label, String icon, int priority, List<Page> pages) {}
    public record Page(int pageId, String id, String label, String url, String icon, int priority, String parentId, String type) {}
}