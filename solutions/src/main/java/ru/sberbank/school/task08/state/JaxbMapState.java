package ru.sberbank.school.task08.state;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "MapState")
public class JaxbMapState<T extends GameObject> implements Savable<T> {
    private String name;
    private List<T> gameObjects;

    private JaxbMapState() {
        gameObjects = null;
        name = null;
    }

    public JaxbMapState(String name, List<T> gameObjects) {
        this.name = name;
        this.gameObjects = gameObjects;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "gameObjects")
    public void setGameObjects(List<T> gameObjects) {
        this.gameObjects = gameObjects;
    }

    public String getName() {
        return name;
    }


    public List<T> getGameObjects() {
        return this.gameObjects;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof JaxbMapState)) {
            return false;
        }
        final JaxbMapState other = (JaxbMapState) o;
        final Object this$gameObjects = this.getGameObjects();
        final Object other$gameObjects = other.getGameObjects();
        return this$gameObjects == null ? other$gameObjects == null : this$gameObjects.equals(other$gameObjects);
    }

    public int hashCode() {
        final int prime = 59;
        int result = 1;
        final Object $gameObjects = this.getGameObjects();
        result = result * prime + ($gameObjects == null ? 43 : $gameObjects.hashCode());
        return result;
    }

    public String toString() {
        return "Name: " + this.name + " MapState(gameObjects=" + this.getGameObjects() + ")";
    }
}
