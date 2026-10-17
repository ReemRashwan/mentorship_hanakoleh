package com.mentorship.hanakoleh.domain.restaurant.controller;

import com.mentorship.hanakoleh.domain.restaurant.dto.MenuHistoryResponse;
import com.mentorship.hanakoleh.domain.restaurant.dto.MenuListResponse;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuHistoryQueryService;
import com.mentorship.hanakoleh.domain.restaurant.service.MenuQueryService;
import com.mentorship.hanakoleh.domain.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurant", description = "Restaurant & menu browsing API")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final MenuQueryService menuQueryService;
    private final MenuHistoryQueryService menuHistoryQueryService;

    public RestaurantController(
            RestaurantService restaurantService,
            MenuQueryService menuQueryService,
            MenuHistoryQueryService menuHistoryQueryService) {
        this.restaurantService = restaurantService;
        this.menuQueryService = menuQueryService;
        this.menuHistoryQueryService = menuHistoryQueryService;
    }

    @GetMapping("/{restaurantId}/menus")
    @Operation(summary = "List a restaurant's menus",
            description = "Returns the restaurant's visible menus (paginated, name asc by default), "
                    + "optionally filtered by category, each with its item count.")
    public ResponseEntity<Page<MenuListResponse>> listMenus(
            @PathVariable Integer restaurantId,
            @RequestParam(value = "category", required = false) String category,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(menuQueryService.listMenus(restaurantId, category, pageable));
    }

    @GetMapping("/{restaurantId}/menus/history")
    @Operation(summary = "View a restaurant's menu change history",
            description = "Audit log of menu changes (newest first). Optionally filter by menuId and "
                    + "a changedAt date range. NOTE: owner authorization not enforced yet (auth pending).")
    public ResponseEntity<Page<MenuHistoryResponse>> listMenuHistory(
            @PathVariable Integer restaurantId,
            @RequestParam(value = "menuId", required = false) Integer menuId,
            @RequestParam(value = "from", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(value = "to", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @PageableDefault(size = 20, sort = "changedAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return ResponseEntity.ok(
                menuHistoryQueryService.getHistory(restaurantId, menuId, from, to, pageable));
    }

    @GetMapping("/{restaurantId}/menus/{menuId}/history")
    @Operation(summary = "View one menu's change history",
            description = "Audit log for a single menu (newest first). Optionally filter by a changedAt "
                    + "date range. NOTE: owner authorization not enforced yet (auth pending).")
    public ResponseEntity<Page<MenuHistoryResponse>> listSingleMenuHistory(
            @PathVariable Integer restaurantId,
            @PathVariable Integer menuId,
            @RequestParam(value = "from", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(value = "to", required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to,
            @PageableDefault(size = 20, sort = "changedAt", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        return ResponseEntity.ok(
                menuHistoryQueryService.getHistory(restaurantId, menuId, from, to, pageable));
    }
}
