import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonMessages { // класс обертка по стандарту JSON содержимое обьекта должно быть завернуто еще в один обьект
    private final List<Message> list;

    public JsonMessages(List<Message> sourceList, int fromIndex) {
        this.list = new ArrayList<>();
        for (int i = fromIndex; i < sourceList.size(); i++)
            list.add(sourceList.get(i));
    }

    public List<Message> getList() {
        return Collections.unmodifiableList(list);
    }
}
