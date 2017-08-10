package edu.wpi.first.shuffleboard.app.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;

import edu.wpi.first.shuffleboard.api.util.GridPoint;
import edu.wpi.first.shuffleboard.api.widget.TileSize;
import edu.wpi.first.shuffleboard.api.widget.Widget;
import edu.wpi.first.shuffleboard.app.components.Tile;
import edu.wpi.first.shuffleboard.app.components.WidgetPane;

import java.util.Map;

import javafx.scene.layout.GridPane;

@AnnotatedTypeAdapter(forType = WidgetPane.class)
public class WidgetPaneSaver implements ElementTypeAdapter<WidgetPane> {

  @Override
  public JsonElement serialize(WidgetPane src, JsonSerializationContext context) {
    JsonObject object = new JsonObject();

    for (Tile<?> tile : src.getTiles()) {
      if (!(tile.getContent() instanceof Widget)) {
        continue; //FIXME
      }
      String x = GridPane.getColumnIndex(tile).toString();
      String y = GridPane.getRowIndex(tile).toString();
      String coordinate = String.join(",", x, y);

      JsonObject tileObject = new JsonObject();
      tileObject.add("size", context.serialize(tile.getSize(), TileSize.class));
      tileObject.add("widget", context.serialize(tile.getContent(), Widget.class));

      object.add(coordinate, tileObject);
    }

    return object;
  }

  @Override
  public WidgetPane deserialize(JsonElement json, JsonDeserializationContext context) throws JsonParseException {
    JsonObject tiles = json.getAsJsonObject();
    WidgetPane pane = new WidgetPane();

    for (Map.Entry<String, JsonElement> tileLocation : tiles.entrySet()) {
      String[] coordPart = tileLocation.getKey().split(",");
      GridPoint coords = new GridPoint(Integer.parseInt(coordPart[0]), Integer.parseInt(coordPart[1]));

      JsonObject tile = tileLocation.getValue().getAsJsonObject();
      TileSize size = context.deserialize(tile.get("size"), TileSize.class);
      Widget widget = context.deserialize(tile.get("widget"), Widget.class);

      pane.addWidget(widget, coords, size);
    }

    return pane;
  }
}
