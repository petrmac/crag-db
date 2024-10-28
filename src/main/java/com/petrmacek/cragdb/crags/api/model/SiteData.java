package com.petrmacek.cragdb.crags.api.model;

import java.util.Set;

public record SiteData(String name, Set<String> sectors, Location location) {
}
