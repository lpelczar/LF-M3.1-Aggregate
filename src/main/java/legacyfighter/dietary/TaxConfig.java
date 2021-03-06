package legacyfighter.dietary;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
public class TaxConfig {

    public TaxConfig() {

    }

    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private String countryReason;

    @Embedded
    private CountryCode countryCode;

    private Instant lastModifiedDate;
    private int currentRulesCount;
    private int maxRulesCount;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TaxRule> taxRules = new ArrayList<>();

    private TaxConfig(CountryCode countryCode, int maxRulesCount) {
        this.countryCode = countryCode;
        this.maxRulesCount = maxRulesCount;
    }

    public static TaxConfig from(String countryCode, int maxRulesCount) {
        return new TaxConfig(CountryCode.from(countryCode), maxRulesCount);
    }

    public void addTaxRule(TaxRule taxRule, Instant moment) {
        if (taxRules.size() >= maxRulesCount) {
            throw new IllegalStateException("Cannot add more rules");
        }
        taxRules.add(taxRule);
        lastModifiedDate = moment;
        currentRulesCount++;
    }

    public void removeTaxRule(TaxRule taxRule, Instant moment) {
        if (taxRules.contains(taxRule)) {
            if (taxRules.size() == 1) {
                throw new IllegalStateException("Last rule in country config");
            }
            taxRules.remove(taxRule);
            lastModifiedDate = moment;
            currentRulesCount--;
        }
    }

    public String getDescription() {
        return description;
    }

    public String getCountryReason() {
        return countryReason;
    }

    public String getCountryCode() {
        return countryCode.asString();
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public int getCurrentRulesCount() {
        return currentRulesCount;
    }

    public int getMaxRulesCount() {
        return maxRulesCount;
    }

    public List<TaxRule> getTaxRules() {
        return Collections.unmodifiableList(taxRules);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxConfig taxConfig = (TaxConfig) o;
        return id.equals(taxConfig.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }
}

@Embeddable
class CountryCode {

    public CountryCode() {

    }

    private String code;

    public String asString() {
        return code;
    }

    private CountryCode(String code) {
        this.code = code;
    }

    static CountryCode from(String code) {
        if (code == null || code.equals("") || code.length() == 1) {
            throw new IllegalStateException("Invalid country code");
        }
        return new CountryCode(code);
    }
}
