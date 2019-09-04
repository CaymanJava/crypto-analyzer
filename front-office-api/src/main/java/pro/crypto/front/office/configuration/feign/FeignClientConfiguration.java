package pro.crypto.front.office.configuration.feign;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleModule;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.nonNull;

@Configuration
public class FeignClientConfiguration {

    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Bean
    public Encoder feignEncoder() {
        return new PageableQueryEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public Module myJacksonModule() {
        return new MyJacksonModule();
    }

    @JsonDeserialize(as = SimplePageImpl.class)
    private interface PageMixIn {
    }

    public static class MyJacksonModule extends SimpleModule {

        @Override
        public void setupModule(Module.SetupContext context) {
            context.setMixInAnnotations(Page.class, PageMixIn.class);
        }
    }

    private static class SimplePageImpl<T> implements Page<T> {

        private final Page<T> delegate;

        public SimplePageImpl(
                @JsonProperty("content") List<T> content,
                @JsonProperty("page") int number,
                @JsonProperty("size") int size,
                @JsonProperty("totalElements") long totalElements) {
            delegate = new PageImpl<>(content, PageRequest.of(number, size), totalElements);
        }

        @JsonProperty
        @Override
        public int getTotalPages() {
            return delegate.getTotalPages();
        }

        @JsonProperty
        @Override
        public long getTotalElements() {
            return delegate.getTotalElements();
        }

        @JsonIgnore
        @Override
        public <U> Page<U> map(Function<? super T, ? extends U> function) {
            return delegate.map(function);
        }

        @JsonProperty("page")
        @Override
        public int getNumber() {
            return delegate.getNumber();
        }

        @JsonProperty
        @Override
        public int getSize() {
            return delegate.getSize();
        }

        @JsonProperty
        @Override
        public int getNumberOfElements() {
            return delegate.getNumberOfElements();
        }

        @JsonProperty
        @Override
        public List<T> getContent() {
            return delegate.getContent();
        }

        @JsonProperty
        @Override
        public boolean hasContent() {
            return delegate.hasContent();
        }

        @JsonIgnore
        @Override
        public Sort getSort() {
            return delegate.getSort();
        }

        @JsonProperty
        @Override
        public boolean isFirst() {
            return delegate.isFirst();
        }

        @JsonProperty
        @Override
        public boolean isLast() {
            return delegate.isLast();
        }

        @JsonIgnore
        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @JsonIgnore
        @Override
        public boolean hasPrevious() {
            return delegate.hasPrevious();
        }

        @JsonIgnore
        @Override
        public Pageable nextPageable() {
            return delegate.nextPageable();
        }

        @JsonIgnore
        @Override
        public Pageable previousPageable() {
            return delegate.previousPageable();
        }

        @JsonIgnore
        @Override
        public Iterator<T> iterator() {
            return delegate.iterator();
        }
    }

    private static class PageableQueryEncoder implements Encoder {

        private final Encoder delegate;

        PageableQueryEncoder(Encoder delegate) {
            this.delegate = delegate;
        }

        @Override
        public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
            if (object instanceof Pageable) {
                Pageable pageable = (Pageable) object;
                template.query("page", pageable.getPageNumber() + "");
                template.query("size", pageable.getPageSize() + "");
                if (nonNull(pageable.getSort())) {
                    Collection<String> existingSorts = template.queries().get("sort");
                    List<String> sortQueries = nonNull(existingSorts) ? new ArrayList<>(existingSorts) : new ArrayList<>();
                    for (Sort.Order order : pageable.getSort()) {
                        sortQueries.add(order.getProperty() + "," + order.getDirection());
                    }
                    template.query("sort", sortQueries);
                }
            } else {
                delegate.encode(object, bodyType, template);
            }
        }

    }

}
