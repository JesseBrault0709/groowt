package groowt.view.web.ast;

import groowt.view.web.antlr.TokenList;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DefaultNodeFactoryTests extends NodeFactoryTests {

    public DefaultNodeFactoryTests() {
        super(nft -> ((DefaultNodeFactoryTests) nft).getNodeFactory());
    }

    protected NodeFactory getNodeFactory() {
        return new DefaultNodeFactory(new TokenList(List.of(this.getStartAndEndToken())));
    }

}
