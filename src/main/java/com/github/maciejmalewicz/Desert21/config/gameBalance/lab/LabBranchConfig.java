package com.github.maciejmalewicz.Desert21.config.gameBalance.lab;

import java.util.List;

public interface LabBranchConfig {
    LabUpgrade getBaseUpgrade();
    List<LabUpgrade> getFirstTierUpgrades();
    List<LabUpgrade> getSecondTierUpgrades();
    LabUpgrade getSuperUpgrade();

    Object getBalanceConfig();
    Object getCostConfig();

    default boolean containsUpgrade(LabUpgrade upgrade) {
        return getBaseUpgrade().equals(upgrade)
                || getFirstTierUpgrades().contains(upgrade)
                || getSecondTierUpgrades().contains(upgrade)
                || getSuperUpgrade().equals(upgrade);
    }

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
