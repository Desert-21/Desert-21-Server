package com.github.maciejmalewicz.Desert21.config.gameBalance.lab;

import java.util.List;

public interface LabBranchConfig {
    LabUpgrade getBaseUpgrade();
    List<LabUpgrade> getFirstTierUpgrades();
    List<LabUpgrade> getSecondTierUpgrades();
    LabUpgrade getSuperUpgrade();

    default boolean isAllowedToUpgrade(LabUpgrade upgrade, List<LabUpgrade> ownedUpgrades) {
        if (upgrade.equals(getBaseUpgrade())) {
            return true;
        }
        if (getFirstTierUpgrades().contains(upgrade)) {
            return ownedUpgrades.stream()
                    .anyMatch(u -> u.equals(getBaseUpgrade()));
        }
        if (getSecondTierUpgrades().contains(upgrade)) {
            return ownedUpgrades.stream()
                    .anyMatch(u -> getFirstTierUpgrades().contains(u));
        }
        if (upgrade.equals(getSuperUpgrade())) {
            return ownedUpgrades.stream()
                    .anyMatch(u -> getSecondTierUpgrades().contains(u));
        }
        return false;
    }
}
