package response;

import model.GameData;

public record ListResult(java.util.Collection<GameData> games) {}