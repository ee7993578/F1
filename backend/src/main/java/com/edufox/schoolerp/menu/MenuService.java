package com.edufox.schoolerp.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository repository;

    public MenuDtos.MenuResponse getMenu(String schoolId, String staffUid, String role) {
        String safeSchoolId = safe(schoolId), safeStaffUid = safe(staffUid);
        String safeRole = safe(role).toLowerCase(Locale.ROOT);
        String schoolName = repository.findSchoolName(safeSchoolId);
        String session = repository.findCurrentSession(safeSchoolId);
        Map<String, Object> profile = repository.findSchoolProfile(safeSchoolId);
        String address = String.join(", ", List.of("address1", "address2", "address3", "address4").stream()
            .map(key -> safe(String.valueOf(profile.getOrDefault(key, ""))))
            .filter(value -> !value.isBlank()).toList());
        String phone = safe(String.valueOf(profile.getOrDefault("phone", "")));
        String logo = safe(String.valueOf(profile.getOrDefault("logo", "")));
        if ("student".equals(safeRole) || "parent".equals(safeRole))
            return studentMenu(safeSchoolId, safeRole, schoolName, session, address, phone, logo);
        return staffMenu(safeSchoolId, safeStaffUid, schoolName, session, address, phone, logo);
    }

    private MenuDtos.MenuResponse studentMenu(String schoolId, String role, String schoolName, String session,
                                               String address, String phone, String logo) {
        String userType = "parent".equals(role) ? "P" : "S";
        List<MenuRepository.StudentPageRow> configuredPages = repository.findStudentPages(schoolId, userType);
        // Some legacy schools store the common portal catalogue only under the
        // student user type.  GroupMaster exposed that same catalogue to a
        // parent login as well, so retain that fallback instead of returning an
        // empty Work group for parents.
        if (configuredPages.isEmpty() && "P".equals(userType)) {
            configuredPages = repository.findStudentPages(schoolId, "S");
        }
        List<MenuDtos.Page> pages = configuredPages.stream()
            .map(p -> new MenuDtos.Page(numericId(p.pageId()), pageKey(p.pageUrl(), p.pageId()), p.pageName(),
                p.pageUrl(), "", p.priority(), "student", "student")).toList();
        return new MenuDtos.MenuResponse(schoolName, session, address, phone, logo,
            List.of(new MenuDtos.Module(0, "student", "Student", "BookOpen", "",
                List.of(new MenuDtos.Group("student-work", "Work", "BookOpen", 1, pages)))));
    }

    private MenuDtos.MenuResponse staffMenu(String schoolId, String staffUid, String schoolName, String session,
                                             String address, String phone, String logo) {
        List<MenuRepository.ModuleRow> moduleRows = repository.findStaffModules(schoolId, staffUid);
        Set<String> allowed = repository.findAllowedPageIds(schoolId, staffUid);
        List<MenuRepository.PageRow> pageRows = repository.findSchoolPages(schoolId);
        List<MenuDtos.Module> modules = new ArrayList<>();
        for (MenuRepository.ModuleRow module : moduleRows) {
            List<MenuRepository.PageRow> rows = pageRows.stream().filter(p -> p.moduleId() == module.moduleId()).toList();
            modules.add(toModule(module, rows, allowed));
        }
        return new MenuDtos.MenuResponse(schoolName, session, address, phone, logo, modules);
    }

    private MenuDtos.Module toModule(MenuRepository.ModuleRow module, List<MenuRepository.PageRow> rows,
                                     Set<String> allowed) {
        Map<String, GroupBuilder> groups = new LinkedHashMap<>();
        Map<String, MenuRepository.PageRow> pageByNewId = new LinkedHashMap<>();
        Set<String> seenPages = new HashSet<>();
        rows.forEach(row -> pageByNewId.put(String.valueOf(row.newPageId()), row));

        for (MenuRepository.PageRow row : rows) {
            boolean permitted = allowed.contains(String.valueOf(row.pageId()))
                || (row.isReport() && allowed.contains(String.valueOf(row.newPageId())));
            if (!permitted) continue;
            String parentId = row.isReport() ? "1" : safe(row.parentId());
            if ("0".equals(parentId) || parentId.isBlank()) parentId = "direct";
            String normalizedPageKey = pageKey(row.pageUrl(), String.valueOf(row.pageId()));
            String dedupeKey = parentId + "|" + normalizedPageKey.toLowerCase(Locale.ROOT);
            if (!seenPages.add(dedupeKey)) continue;
            MenuRepository.PageRow parent = pageByNewId.get(parentId);
            String groupLabel = parent == null ? (row.isReport() ? "Reports" : "Work") : parent.efoxLabel();
            String groupIcon = parent == null ? "Folder" : safe(parent.icon());
            int groupPriority = parent == null ? row.efoxPriority() : parent.efoxPriority();
            GroupBuilder group = groups.computeIfAbsent(parentId,
                key -> new GroupBuilder(key, normalizeGroupName(groupLabel), groupIcon, groupPriority));
            group.pages.add(new MenuDtos.Page(row.pageId(), normalizedPageKey,
                row.isReport() ? row.reportLabel() : row.efoxLabel(), row.pageUrl(), safe(row.icon()),
                row.isReport() ? row.reportPriority() : row.efoxPriority(), parentId,
                row.isReport() ? "reports" : safe(row.type())));
        }

        List<MenuDtos.Group> resultGroups = groups.values().stream().filter(g -> !g.pages.isEmpty())
            .sorted(Comparator.comparingInt(g -> g.priority)).map(GroupBuilder::toDto).toList();
        return new MenuDtos.Module(module.moduleId(), String.valueOf(module.moduleId()),
            normalizeModuleName(module.moduleName()), safe(module.icon()), safe(module.redirectUrl()), resultGroups);
    }

    private static String normalizeModuleName(String value) {
        return switch (safe(value).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "")) {
            case "fees" -> "Fee";
            case "examination" -> "Exam Master";
            case "staffmanagement" -> "Staff";
            case "admission" -> "Student";
            case "webadmin" -> "Website";
            case "schooladmin" -> "Master Setting";
            case "expense", "tuckshop" -> "Canteen";
            default -> safe(value);
        };
    }

    private static String normalizeGroupName(String value) {
        return switch (safe(value).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "")) {
            case "transaction", "transactions", "work" -> "Work";
            case "setting", "settings", "setup" -> "Setup";
            default -> safe(value);
        };
    }

    private static String pageKey(String url, String fallback) {
        String value = safe(url);
        int query = value.indexOf('?'); if (query >= 0) value = value.substring(0, query);
        int hash = value.indexOf('#'); if (hash >= 0) value = value.substring(0, hash);
        int slash = Math.max(value.lastIndexOf('/'), value.lastIndexOf('\\'));
        String file = slash >= 0 ? value.substring(slash + 1) : value;
        if (file.endsWith(".aspx")) file = file.substring(0, file.length() - 5);
        return file.isBlank() ? fallback : file;
    }

    private static int numericId(String value) {
        try { return Integer.parseInt(value); } catch (NumberFormatException ignored) { return 0; }
    }
    private static String safe(String value) { return value == null ? "" : value.trim(); }

    private static final class GroupBuilder {
        private final String id, label, icon; private final int priority;
        private final List<MenuDtos.Page> pages = new ArrayList<>();
        private GroupBuilder(String id, String label, String icon, int priority) {
            this.id=id; this.label=label; this.icon=icon; this.priority=priority;
        }
        private MenuDtos.Group toDto() {
            pages.sort(Comparator.comparingInt(MenuDtos.Page::priority)
                .thenComparing(MenuDtos.Page::label, String.CASE_INSENSITIVE_ORDER));
            return new MenuDtos.Group(id, label, icon, priority, List.copyOf(pages));
        }
    }
}
