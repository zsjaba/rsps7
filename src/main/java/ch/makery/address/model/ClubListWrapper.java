package ch.makery.address.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Вспомогательный класс для обёртывания списка адресатов.
 * Используется для сохранения списка адресатов в XML.
 *
 * @author Marco Jakob
 */
@XmlRootElement(name = "clubs")
public class ClubListWrapper {

    private List<Club> clubs;

    @XmlElement(name = "club")
    public List<Club> getClubs() {
        return clubs;
    }

    public void setClubs(List<Club> clubs) {
        this.clubs = clubs;
    }
}