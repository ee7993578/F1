package com.edufox.schoolerp.canteen.work.canteen;

import com.edufox.schoolerp.canteen.shared.canteen.CanteenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Work pages for the Canteen module. Business/data logic remains shared. */
@RestController
@RequestMapping("/api/canteen")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
public class CanteenWorkController {
    private final CanteenService service;

    private static Claims claims(Authentication a) { return (Claims) a.getDetails(); }
    private static String school(Authentication a) { return String.valueOf(claims(a).get("schoolId")); }
    private static String staff(Authentication a) { return String.valueOf(claims(a).get("staffUid")); }
    private static String text(Map<String, Object> body, String key) {
        Object value = body.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }
    private String session(String requested, Authentication a) {
        String value = requested == null ? "" : requested.trim();
        if (!value.isBlank() && !"0".equals(value)) return value;
        return String.valueOf(service.bootstrap(school(a), "", staff(a)).getOrDefault("session", ""));
    }

    @GetMapping("/bootstrap")
    public Map<String, Object> bootstrap(@RequestParam(defaultValue = "") String session, Authentication a) {
        return service.bootstrap(school(a), session, staff(a));
    }

    @GetMapping("/expense-groups") public List<Map<String, Object>> groups(Authentication a) { return service.groups(school(a)); }
    @PostMapping("/expense-groups") public Map<String, Object> addGroup(@RequestBody Map<String, Object> b, Authentication a) { return service.saveGroup("", b, school(a), staff(a)); }
    @PutMapping("/expense-groups/{id}") public Map<String, Object> editGroup(@PathVariable String id, @RequestBody Map<String, Object> b, Authentication a) { return service.saveGroup(id, b, school(a), staff(a)); }
    @DeleteMapping("/expense-groups/{id}") public Map<String, Object> deleteGroup(@PathVariable String id, Authentication a) { return service.deleteGroup(id, school(a)); }

    @GetMapping("/expense-names") public List<Map<String, Object>> names(Authentication a) { return service.names(school(a)); }
    @PostMapping("/expense-names") public Map<String, Object> addName(@RequestBody Map<String, Object> b, Authentication a) { return service.saveName("", b, school(a), staff(a)); }
    @PutMapping("/expense-names/{id}") public Map<String, Object> editName(@PathVariable String id, @RequestBody Map<String, Object> b, Authentication a) { return service.saveName(id, b, school(a), staff(a)); }
    @DeleteMapping("/expense-names/{id}") public Map<String, Object> deleteName(@PathVariable String id, Authentication a) { return service.deleteName(id, school(a)); }

    @GetMapping("/product-groups") public List<Map<String, Object>> productGroups(Authentication a) { return service.productGroups(school(a)); }
    @PostMapping("/product-groups") public Map<String, Object> addProductGroup(@RequestBody Map<String, Object> b, Authentication a) { return service.saveProductGroup("", b, school(a), staff(a)); }
    @PutMapping("/product-groups/{id}") public Map<String, Object> editProductGroup(@PathVariable String id, @RequestBody Map<String, Object> b, Authentication a) { return service.saveProductGroup(id, b, school(a), staff(a)); }
    @DeleteMapping("/product-groups/{id}") public Map<String, Object> deleteProductGroup(@PathVariable String id, Authentication a) { return service.deleteProductGroup(id, school(a)); }

    @GetMapping("/products") public List<Map<String, Object>> products(@RequestParam(defaultValue = "") String session, Authentication a) { return service.products(school(a), session(session, a)); }
    @PostMapping("/products") public Map<String, Object> addProduct(@RequestBody Map<String, Object> b, Authentication a) { return service.saveProduct("", b, school(a), session(text(b, "session"), a), staff(a)); }
    @PutMapping("/products/{id}") public Map<String, Object> editProduct(@PathVariable String id, @RequestBody Map<String, Object> b, Authentication a) { return service.saveProduct(id, b, school(a), session(text(b, "session"), a), staff(a)); }
    @DeleteMapping("/products/{id}") public Map<String, Object> deleteProduct(@PathVariable String id, Authentication a) { return service.deleteProduct(id, school(a)); }

    @GetMapping("/credit-balances") public List<Map<String, Object>> credits(@RequestParam(defaultValue = "") String session, Authentication a) { return service.credits(school(a), session(session, a)); }
    @PutMapping("/credit-balances/{registrationNo}") public Map<String, Object> updateCredit(@PathVariable String registrationNo, @RequestBody Map<String, Object> b, Authentication a) { return service.updateCredit(registrationNo, session(text(b, "session"), a), b, school(a), staff(a)); }

    @GetMapping("/students") public List<Map<String, Object>> students(@RequestParam String session, @RequestParam(defaultValue = "0") String secId, Authentication a) { return service.students(school(a), session, secId); }
    @GetMapping("/student") public Map<String, Object> student(@RequestParam String session, @RequestParam String registrationNo, Authentication a) { return service.student(school(a), session, registrationNo); }
    @GetMapping("/registration-suggestions") public List<Map<String, Object>> suggestions(@RequestParam String session, @RequestParam(defaultValue = "") String prefix, Authentication a) { return service.suggestions(school(a), session, prefix); }
    @GetMapping("/expense-history") public Map<String, Object> history(@RequestParam String session, @RequestParam String registrationNo, Authentication a) { return service.history(school(a), session, registrationNo); }

    @PostMapping("/assign-expenses") public Map<String, Object> saveAssignments(@RequestBody Map<String, Object> b, Authentication a) { return service.saveAssignments(school(a), session(text(b, "session"), a), staff(a), b); }
    @PutMapping("/assign-expenses/{id}") public Map<String, Object> updateAssignment(@PathVariable String id, @RequestParam String session, @RequestParam String registrationNo, @RequestBody Map<String, Object> b, Authentication a) { return service.updateAssignment(id, school(a), session, registrationNo, b); }
    @DeleteMapping("/assign-expenses/{id}") public Map<String, Object> deleteAssignment(@PathVariable String id, @RequestParam String session, @RequestParam String registrationNo, Authentication a) { return service.deleteAssignment(id, school(a), session, registrationNo); }
}
