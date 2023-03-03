package ed.fumes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*

SLEF json schema from example:
```
{
    "$schema": "https://json-schema.org/draft/2019-09/schema",
    "$id": "http://example.com/example.json",
    "type": "object",
    "default": {},
    "title": "Root Schema",
    "required": [
        "header",
        "data"
    ],
    "properties": {
        "header": {
            "type": "object",
            "default": {},
            "title": "The header Schema",
            "required": [
                "appName",
                "appVersion",
                "appURL"
            ],
            "properties": {
                "appName": {
                    "type": "string",
                    "default": "",
                    "title": "The appName Schema",
                    "examples": [
                        "EDSY"
                    ]
                },
                "appVersion": {
                    "type": "integer",
                    "default": 0,
                    "title": "The appVersion Schema",
                    "examples": [
                        308149912
                    ]
                },
                "appURL": {
                    "type": "string",
                    "default": "",
                    "title": "The appURL Schema",
                    "examples": [
                        "https://edsy.org/#/L=H_5XfyPl0H4C0S00,,mpXCjwIK2G_W0CjwJK2G_W0CjwJK2G_W0,9p3H05GGu0A72HK4J_W0ARqGK3I_W0Ag_IK5JwO0Ax4HK2W_W0B98IK2n_W0BQKHK2Wxz0BeE1K,,0DI1K0DI1K0AA1K0AA1K0AA1K50U0K34a0K0731K0nG3K0nF3K,The_0Paper_0Machette,JI_D27C"
                    ]
                }
            },
            "examples": [{
                "appName": "EDSY",
                "appVersion": 308149912,
                "appURL": "https://edsy.org/#/L=H_5XfyPl0H4C0S00,,mpXCjwIK2G_W0CjwJK2G_W0CjwJK2G_W0,9p3H05GGu0A72HK4J_W0ARqGK3I_W0Ag_IK5JwO0Ax4HK2W_W0B98IK2n_W0BQKHK2Wxz0BeE1K,,0DI1K0DI1K0AA1K0AA1K0AA1K50U0K34a0K0731K0nG3K0nF3K,The_0Paper_0Machette,JI_D27C"
            }]
        },
        "data": {
            "type": "object",
            "default": {},
            "title": "The data Schema",
            "required": [
                "event",
                "Ship",
                "ShipName",
                "ShipIdent",
                "HullValue",
                "ModulesValue",
                "UnladenMass",
                "CargoCapacity",
                "MaxJumpRange",
                "FuelCapacity",
                "Rebuy",
                "Modules"
            ],
            "properties": {
                "event": {
                    "type": "string",
                    "default": "",
                    "title": "The event Schema",
                    "examples": [
                        "Loadout"
                    ]
                },
                "Ship": {
                    "type": "string",
                    "default": "",
                    "title": "The Ship Schema",
                    "examples": [
                        "cutter"
                    ]
                },
                "ShipName": {
                    "type": "string",
                    "default": "",
                    "title": "The ShipName Schema",
                    "examples": [
                        "The Paper Machette"
                    ]
                },
                "ShipIdent": {
                    "type": "string",
                    "default": "",
                    "title": "The ShipIdent Schema",
                    "examples": [
                        "JI-27C"
                    ]
                },
                "HullValue": {
                    "type": "integer",
                    "default": 0,
                    "title": "The HullValue Schema",
                    "examples": [
                        175924977
                    ]
                },
                "ModulesValue": {
                    "type": "integer",
                    "default": 0,
                    "title": "The ModulesValue Schema",
                    "examples": [
                        58227409
                    ]
                },
                "UnladenMass": {
                    "type": "number",
                    "default": 0.0,
                    "title": "The UnladenMass Schema",
                    "examples": [
                        1301.17536
                    ]
                },
                "CargoCapacity": {
                    "type": "integer",
                    "default": 0,
                    "title": "The CargoCapacity Schema",
                    "examples": [
                        720
                    ]
                },
                "MaxJumpRange": {
                    "type": "number",
                    "default": 0.0,
                    "title": "The MaxJumpRange Schema",
                    "examples": [
                        52.128401
                    ]
                },
                "FuelCapacity": {
                    "type": "object",
                    "default": {},
                    "title": "The FuelCapacity Schema",
                    "required": [
                        "Main",
                        "Reserve"
                    ],
                    "properties": {
                        "Main": {
                            "type": "integer",
                            "default": 0,
                            "title": "The Main Schema",
                            "examples": [
                                64
                            ]
                        },
                        "Reserve": {
                            "type": "number",
                            "default": 0.0,
                            "title": "The Reserve Schema",
                            "examples": [
                                1.16
                            ]
                        }
                    },
                    "examples": [{
                        "Main": 64,
                        "Reserve": 1.16
                    }]
                },
                "Rebuy": {
                    "type": "integer",
                    "default": 0,
                    "title": "The Rebuy Schema",
                    "examples": [
                        11707619
                    ]
                },
                "Modules": {
                    "type": "array",
                    "default": [],
                    "title": "The Modules Schema",
                    "items": {
                        "type": "object",
                        "title": "A Schema",
                        "required": [
                            "Slot",
                            "Item",
                            "On",
                            "Priority",
                            "Value",
                            "Engineering"
                        ],
                        "properties": {
                            "Slot": {
                                "type": "string",
                                "title": "The Slot Schema",
                                "examples": [
                                    "CargoHatch",
                                    "TinyHardpoint6",
                                    "TinyHardpoint7",
                                    "TinyHardpoint8",
                                    "Armour",
                                    "PowerPlant",
                                    "MainEngines",
                                    "FrameShiftDrive",
                                    "LifeSupport",
                                    "PowerDistributor",
                                    "Radar",
                                    "FuelTank",
                                    "Slot01_Size8",
                                    "Slot02_Size8",
                                    "Slot03_Size6",
                                    "Slot04_Size6",
                                    "Slot05_Size6",
                                    "Slot06_Size5",
                                    "Slot07_Size5",
                                    "Slot08_Size4",
                                    "Slot09_Size3",
                                    "Slot10_Size1"
                                ]
                            },
                            "Item": {
                                "type": "string",
                                "title": "The Item Schema",
                                "examples": [
                                    "modularcargobaydoor",
                                    "hpt_heatsinklauncher_turret_tiny",
                                    "cutter_armour_grade1",
                                    "int_powerplant_size4_class5",
                                    "int_engine_size7_class2",
                                    "int_hyperdrive_size7_class5",
                                    "int_lifesupport_size7_class2",
                                    "int_powerdistributor_size6_class2",
                                    "int_sensors_size7_class2",
                                    "int_fueltank_size6_class3",
                                    "int_cargorack_size8_class1",
                                    "int_cargorack_size6_class1",
                                    "int_fuelscoop_size5_class5",
                                    "int_guardianfsdbooster_size5",
                                    "int_corrosionproofcargorack_size4_class1",
                                    "int_supercruiseassist",
                                    "int_dockingcomputer_advanced"
                                ]
                            },
                            "On": {
                                "type": "boolean",
                                "title": "The On Schema",
                                "examples": [
                                    true
                                ]
                            },
                            "Priority": {
                                "type": "integer",
                                "title": "The Priority Schema",
                                "examples": [
                                    0,
                                    2,
                                    3,
                                    1
                                ]
                            },
                            "Value": {
                                "type": "integer",
                                "title": "The Value Schema",
                                "examples": [
                                    2520,
                                    0,
                                    1037686,
                                    1367712,
                                    36928159,
                                    448445,
                                    160157,
                                    245938,
                                    2757506,
                                    261065,
                                    6533057,
                                    4667832,
                                    67918,
                                    6566,
                                    9727
                                ]
                            },
                            "Engineering": {
                                "type": "object",
                                "title": "The Engineering Schema",
                                "required": [
                                    "BlueprintName",
                                    "Level",
                                    "Quality",
                                    "Modifiers",
                                    "ExperimentalEffect"
                                ],
                                "properties": {
                                    "BlueprintName": {
                                        "type": "string",
                                        "title": "The BlueprintName Schema",
                                        "examples": [
                                            "Misc_HeatSinkCapacity",
                                            "Armour_HeavyDuty",
                                            "PowerPlant_Stealth",
                                            "Engine_Tuned",
                                            "FSD_LongRange",
                                            "Misc_LightWeight",
                                            "PowerDistributor_HighFrequency",
                                            "Sensor_LightWeight"
                                        ]
                                    },
                                    "Level": {
                                        "type": "integer",
                                        "title": "The Level Schema",
                                        "examples": [
                                            1,
                                            5,
                                            2,
                                            3
                                        ]
                                    },
                                    "Quality": {
                                        "type": [
                                            "integer",
                                            "number"
                                        ],
                                        "title": "The Quality Schema",
                                        "examples": [
                                            1,
                                            0.27,
                                            0.934,
                                            0.9593
                                        ]
                                    },
                                    "Modifiers": {
                                        "type": "array",
                                        "title": "The Modifiers Schema",
                                        "items": {
                                            "type": "object",
                                            "title": "A Schema",
                                            "required": [
                                                "Label",
                                                "Value",
                                                "OriginalValue"
                                            ],
                                            "properties": {
                                                "Label": {
                                                    "type": "string",
                                                    "title": "The Label Schema",
                                                    "examples": [
                                                        "Mass",
                                                        "AmmoMaximum",
                                                        "ReloadTime",
                                                        "DefenceModifierHealthMultiplier",
                                                        "KineticResistance",
                                                        "ThermicResistance",
                                                        "ExplosiveResistance",
                                                        "PowerCapacity",
                                                        "HeatEfficiency",
                                                        "Integrity",
                                                        "PowerDraw",
                                                        "EngineOptimalMass",
                                                        "EngineOptPerformance",
                                                        "EngineHeatRate",
                                                        "FSDOptimalMass",
                                                        "WeaponsCapacity",
                                                        "WeaponsRecharge",
                                                        "EnginesCapacity",
                                                        "EnginesRecharge",
                                                        "SystemsCapacity",
                                                        "SystemsRecharge",
                                                        "SensorTargetScanAngle"
                                                    ]
                                                },
                                                "Value": {
                                                    "type": [
                                                        "number",
                                                        "integer"
                                                    ],
                                                    "title": "The Value Schema",
                                                    "examples": [
                                                        2.6,
                                                        3,
                                                        15,
                                                        131.03,
                                                        -
                                                        14.876,
                                                        4.27,
                                                        -
                                                        34.022,
                                                        4.68,
                                                        15.132,
                                                        0.3,
                                                        88.2,
                                                        7.9344,
                                                        1458,
                                                        133.12,
                                                        0.572,
                                                        104,
                                                        128.248,
                                                        1.035,
                                                        4333.8672,
                                                        14.4,
                                                        94.4,
                                                        39.8088,
                                                        4.85394,
                                                        27.2376,
                                                        2.98704,
                                                        20.99536,
                                                        84,
                                                        27
                                                    ]
                                                },
                                                "OriginalValue": {
                                                    "type": [
                                                        "number",
                                                        "integer"
                                                    ],
                                                    "title": "The OriginalValue Schema",
                                                    "examples": [
                                                        1.3,
                                                        2,
                                                        10,
                                                        80,
                                                        -
                                                        20,
                                                        0,
                                                        -
                                                        40,
                                                        5,
                                                        15.6,
                                                        0.4,
                                                        105,
                                                        6.84,
                                                        1620,
                                                        100,
                                                        164,
                                                        0.9,
                                                        2700,
                                                        32,
                                                        118,
                                                        38,
                                                        3.9,
                                                        26,
                                                        2.4,
                                                        30
                                                    ]
                                                }
                                            },
                                            "examples": [{
                                                "Label": "Mass",
                                                "Value": 2.6,
                                                "OriginalValue": 1.3
                                            },
                                            {
                                                "Label": "AmmoMaximum",
                                                "Value": 3,
                                                "OriginalValue": 2
                                            },
                                            {
                                                "Label": "ReloadTime",
                                                "Value": 15,
                                                "OriginalValue": 10
                                            },
                                            {
                                                "Label": "Mass",
                                                "Value": 2.6,
                                                "OriginalValue": 1.3
                                            },
                                            {
                                                "Label": "AmmoMaximum",
                                                "Value": 3,
                                                "OriginalValue": 2
                                            },
                                            {
                                                "Label": "ReloadTime",
                                                "Value": 15,
                                                "OriginalValue": 10
                                            },
                                            {
                                                "Label": "Mass",
                                                "Value": 2.6,
                                                "OriginalValue": 1.3
                                            },
                                            {
                                                "Label": "AmmoMaximum",
                                                "Value": 3,
                                                "OriginalValue": 2
                                            },
                                            {
                                                "Label": "ReloadTime",
                                                "Value": 15,
                                                "OriginalValue": 10
                                            },
                                            {
                                                "Label": "DefenceModifierHealthMultiplier",
                                                "Value": 131.03,
                                                "OriginalValue": 80
                                            },
                                            {
                                                "Label": "KineticResistance",
                                                "Value":
                                                    -
                                                    14.876,
                                                "OriginalValue":
                                                    -
                                                    20
                                            },
                                            {
                                                "Label": "ThermicResistance",
                                                "Value": 4.27,
                                                "OriginalValue": 0
                                            },
                                            {
                                                "Label": "ExplosiveResistance",
                                                "Value":
                                                    -
                                                    34.022,
                                                "OriginalValue":
                                                    -
                                                    40
                                            },
                                            {
                                                "Label": "Mass",
                                                "Value": 4.68,
                                                "OriginalValue": 5
                                            },
                                            {
                                                "Label": "PowerCapacity",
                                                "Value": 15.132,
                                                "OriginalValue": 15.6
                                            },
                                            {
                                                "Label": "HeatEfficiency",
                                                "Value": 0.3,
                                                "OriginalValue": 0.4
                                            },
                                            {
                                                "Label": "Integrity",
                                                "Value": 88.2,
                                                "OriginalValue": 105
                                            },
                                            {
                                                "Label": "PowerDraw",
                                                "Value": 7.9344,
                                                "OriginalValue": 6.84
                                            },
                                            {
                                                "Label": "EngineOptimalMass",
                                                "Value": 1458,
                                                "OriginalValue": 1620
                                            },
                                            {
                                                "Label": "EngineOptPerformance",
                                                "Value": 133.12,
                                                "OriginalValue": 100
                                            },
                                            {
                                                "Label": "EngineHeatRate",
                                                "Value": 0.572,
                                                "OriginalValue": 1.3
                                            },
                                            {
                                                "Label": "Mass",
                                                "Value": 104,
                                                "OriginalValue": 80
                                            },
                                            {
                                                "Label": "Integrity",
                                                "Value": 128.248,
                                                "OriginalValue": 164
                                            },
                                            {
                                                "Label": "PowerDraw",
                                                "Value": 1.035,
                                                "OriginalValue": 0.9
                                            },
                                            {
                                                "Label": "FSDOptimalMass",
                                                "Value": 4333.8672,
                                                "OriginalValue": 2700
                                            },
                                            {
                                                "Label": "Mass",
                                                "Value": 14.4,
                                                "OriginalValue": 32
                                            },
                                            {
                                                "Label": "Integrity",
                                                "Value": 94.4,
                                                "OriginalValue": 118
                                            },
                                            {
                                                "Label": "WeaponsCapacity",
                                                "Value": 39.8088,
                                                "OriginalValue": 38
                                            },
                                            {
                                                "Label": "WeaponsRecharge",
                                                "Value": 4.85394,
                                                "OriginalValue": 3.9
                                            },
                                            {
                                                "Label": "EnginesCapacity",
                                                "Value": 27.2376,
                                                "OriginalValue": 26
                                            },
                                            {
                                                "Label": "EnginesRecharge",
                                                "Value": 2.98704,
                                                "OriginalValue": 2.4
                                            },
                                            {
                                                "Label": "SystemsCapacity",
                                                "Value": 27.2376,
                                                "OriginalValue": 26
                                            },
                                            {
                                                "Label": "SystemsRecharge",
                                                "Value": 2.98704,
                                                "OriginalValue": 2.4
                                            },
                                            {
                                                "Label": "Mass",
                                                "Value": 20.99536,
                                                "OriginalValue": 32
                                            },
                                            {
                                                "Label": "Integrity",
                                                "Value": 84,
                                                "OriginalValue": 105
                                            },
                                            {
                                                "Label": "SensorTargetScanAngle",
                                                "Value": 27,
                                                "OriginalValue": 30
                                            }]
                                        },
                                        "examples": [
                                            [{
                                                "Label": "Mass",
                                                "Value": 2.6,
                                                "OriginalValue": 1.3
                                            },
                                            {
                                                "Label": "AmmoMaximum",
                                                "Value": 3,
                                                "OriginalValue": 2
                                            },
                                            {
                                                "Label": "ReloadTime",
                                                "Value": 15,
                                                "OriginalValue": 10
                                            }],
                                            [{
                                                "Label": "Mass",
                                                "Value": 2.6,
                                                "OriginalValue": 1.3
                                            },
                                            {
                                                "Label": "AmmoMaximum",
                                                "Value": 3,
                                                "OriginalValue": 2
                                            },
                                            {
                                                "Label": "ReloadTime",
                                                "Value": 15,
                                                "OriginalValue": 10
                                            }],
                                            [{
                                                "Label": "Mass",
                                                "Value": 2.6,
                                                "OriginalValue": 1.3
                                            },
                                            {
                                                "Label": "AmmoMaximum",
                                                "Value": 3,
                                                "OriginalValue": 2
                                            },
                                            {
                                                "Label": "ReloadTime",
                                                "Value": 15,
                                                "OriginalValue": 10
                                            }],
                                            [{
                                                "Label": "DefenceModifierHealthMultiplier",
                                                "Value": 131.03,
                                                "OriginalValue": 80
                                            },
                                            {
                                                "Label": "KineticResistance",
                                                "Value":
                                                    -
                                                    14.876,
                                                "OriginalValue":
                                                    -
                                                    20
                                            },
                                            {
                                                "Label": "ThermicResistance",
                                                "Value": 4.27,
                                                "OriginalValue": 0
                                            },
                                            {
                                                "Label": "ExplosiveResistance",
                                                "Value":
                                                    -
                                                    34.022,
                                                "OriginalValue":
                                                    -
                                                    40
                                            }],
                                            [{
                                                "Label": "Mass",
                                                "Value": 4.68,
                                                "OriginalValue": 5
                                            },
                                            {
                                                "Label": "PowerCapacity",
                                                "Value": 15.132,
                                                "OriginalValue": 15.6
                                            },
                                            {
                                                "Label": "HeatEfficiency",
                                                "Value": 0.3,
                                                "OriginalValue": 0.4
                                            }],
                                            [{
                                                "Label": "Integrity",
                                                "Value": 88.2,
                                                "OriginalValue": 105
                                            },
                                            {
                                                "Label": "PowerDraw",
                                                "Value": 7.9344,
                                                "OriginalValue": 6.84
                                            },
                                            {
                                                "Label": "EngineOptimalMass",
                                                "Value": 1458,
                                                "OriginalValue": 1620
                                            },
                                            {
                                                "Label": "EngineOptPerformance",
                                                "Value": 133.12,
                                                "OriginalValue": 100
                                            },
                                            {
                                                "Label": "EngineHeatRate",
                                                "Value": 0.572,
                                                "OriginalValue": 1.3
                                            }],
                                            [{
                                                "Label": "Mass",
                                                "Value": 104,
                                                "OriginalValue": 80
                                            },
                                            {
                                                "Label": "Integrity",
                                                "Value": 128.248,
                                                "OriginalValue": 164
                                            },
                                            {
                                                "Label": "PowerDraw",
                                                "Value": 1.035,
                                                "OriginalValue": 0.9
                                            },
                                            {
                                                "Label": "FSDOptimalMass",
                                                "Value": 4333.8672,
                                                "OriginalValue": 2700
                                            }],
                                            [{
                                                "Label": "Mass",
                                                "Value": 14.4,
                                                "OriginalValue": 32
                                            },
                                            {
                                                "Label": "Integrity",
                                                "Value": 94.4,
                                                "OriginalValue": 118
                                            }],
                                            [{
                                                "Label": "WeaponsCapacity",
                                                "Value": 39.8088,
                                                "OriginalValue": 38
                                            },
                                            {
                                                "Label": "WeaponsRecharge",
                                                "Value": 4.85394,
                                                "OriginalValue": 3.9
                                            },
                                            {
                                                "Label": "EnginesCapacity",
                                                "Value": 27.2376,
                                                "OriginalValue": 26
                                            },
                                            {
                                                "Label": "EnginesRecharge",
                                                "Value": 2.98704,
                                                "OriginalValue": 2.4
                                            },
                                            {
                                                "Label": "SystemsCapacity",
                                                "Value": 27.2376,
                                                "OriginalValue": 26
                                            },
                                            {
                                                "Label": "SystemsRecharge",
                                                "Value": 2.98704,
                                                "OriginalValue": 2.4
                                            }],
                                            [{
                                                "Label": "Mass",
                                                "Value": 20.99536,
                                                "OriginalValue": 32
                                            },
                                            {
                                                "Label": "Integrity",
                                                "Value": 84,
                                                "OriginalValue": 105
                                            },
                                            {
                                                "Label": "SensorTargetScanAngle",
                                                "Value": 27,
                                                "OriginalValue": 30
                                            }]
                                        ]
                                    },
                                    "ExperimentalEffect": {
                                        "type": "string",
                                        "title": "The ExperimentalEffect Schema",
                                        "examples": [
                                            "special_powerplant_lightweight",
                                            "special_engine_overloaded",
                                            "special_fsd_heavy",
                                            "special_powerdistributor_capacity"
                                        ]
                                    }
                                },
                                "examples": [{
                                    "BlueprintName": "Misc_HeatSinkCapacity",
                                    "Level": 1,
                                    "Quality": 1,
                                    "Modifiers": [{
                                        "Label": "Mass",
                                        "Value": 2.6,
                                        "OriginalValue": 1.3
                                    },
                                    {
                                        "Label": "AmmoMaximum",
                                        "Value": 3,
                                        "OriginalValue": 2
                                    },
                                    {
                                        "Label": "ReloadTime",
                                        "Value": 15,
                                        "OriginalValue": 10
                                    }]
                                },
                                {
                                    "BlueprintName": "Misc_HeatSinkCapacity",
                                    "Level": 1,
                                    "Quality": 1,
                                    "Modifiers": [{
                                        "Label": "Mass",
                                        "Value": 2.6,
                                        "OriginalValue": 1.3
                                    },
                                    {
                                        "Label": "AmmoMaximum",
                                        "Value": 3,
                                        "OriginalValue": 2
                                    },
                                    {
                                        "Label": "ReloadTime",
                                        "Value": 15,
                                        "OriginalValue": 10
                                    }]
                                },
                                {
                                    "BlueprintName": "Misc_HeatSinkCapacity",
                                    "Level": 1,
                                    "Quality": 1,
                                    "Modifiers": [{
                                        "Label": "Mass",
                                        "Value": 2.6,
                                        "OriginalValue": 1.3
                                    },
                                    {
                                        "Label": "AmmoMaximum",
                                        "Value": 3,
                                        "OriginalValue": 2
                                    },
                                    {
                                        "Label": "ReloadTime",
                                        "Value": 15,
                                        "OriginalValue": 10
                                    }]
                                },
                                {
                                    "BlueprintName": "Armour_HeavyDuty",
                                    "Level": 5,
                                    "Quality": 0.27,
                                    "Modifiers": [{
                                        "Label": "DefenceModifierHealthMultiplier",
                                        "Value": 131.03,
                                        "OriginalValue": 80
                                    },
                                    {
                                        "Label": "KineticResistance",
                                        "Value":
                                            -
                                            14.876,
                                        "OriginalValue":
                                            -
                                            20
                                    },
                                    {
                                        "Label": "ThermicResistance",
                                        "Value": 4.27,
                                        "OriginalValue": 0
                                    },
                                    {
                                        "Label": "ExplosiveResistance",
                                        "Value":
                                            -
                                            34.022,
                                        "OriginalValue":
                                            -
                                            40
                                    }]
                                },
                                {
                                    "BlueprintName": "PowerPlant_Stealth",
                                    "Level": 1,
                                    "Quality": 1,
                                    "ExperimentalEffect": "special_powerplant_lightweight",
                                    "Modifiers": [{
                                        "Label": "Mass",
                                        "Value": 4.68,
                                        "OriginalValue": 5
                                    },
                                    {
                                        "Label": "PowerCapacity",
                                        "Value": 15.132,
                                        "OriginalValue": 15.6
                                    },
                                    {
                                        "Label": "HeatEfficiency",
                                        "Value": 0.3,
                                        "OriginalValue": 0.4
                                    }]
                                },
                                {
                                    "BlueprintName": "Engine_Tuned",
                                    "Level": 5,
                                    "Quality": 1,
                                    "ExperimentalEffect": "special_engine_overloaded",
                                    "Modifiers": [{
                                        "Label": "Integrity",
                                        "Value": 88.2,
                                        "OriginalValue": 105
                                    },
                                    {
                                        "Label": "PowerDraw",
                                        "Value": 7.9344,
                                        "OriginalValue": 6.84
                                    },
                                    {
                                        "Label": "EngineOptimalMass",
                                        "Value": 1458,
                                        "OriginalValue": 1620
                                    },
                                    {
                                        "Label": "EngineOptPerformance",
                                        "Value": 133.12,
                                        "OriginalValue": 100
                                    },
                                    {
                                        "Label": "EngineHeatRate",
                                        "Value": 0.572,
                                        "OriginalValue": 1.3
                                    }]
                                },
                                {
                                    "BlueprintName": "FSD_LongRange",
                                    "Level": 5,
                                    "Quality": 0.934,
                                    "ExperimentalEffect": "special_fsd_heavy",
                                    "Modifiers": [{
                                        "Label": "Mass",
                                        "Value": 104,
                                        "OriginalValue": 80
                                    },
                                    {
                                        "Label": "Integrity",
                                        "Value": 128.248,
                                        "OriginalValue": 164
                                    },
                                    {
                                        "Label": "PowerDraw",
                                        "Value": 1.035,
                                        "OriginalValue": 0.9
                                    },
                                    {
                                        "Label": "FSDOptimalMass",
                                        "Value": 4333.8672,
                                        "OriginalValue": 2700
                                    }]
                                },
                                {
                                    "BlueprintName": "Misc_LightWeight",
                                    "Level": 2,
                                    "Quality": 1,
                                    "Modifiers": [{
                                        "Label": "Mass",
                                        "Value": 14.4,
                                        "OriginalValue": 32
                                    },
                                    {
                                        "Label": "Integrity",
                                        "Value": 94.4,
                                        "OriginalValue": 118
                                    }]
                                },
                                {
                                    "BlueprintName": "PowerDistributor_HighFrequency",
                                    "Level": 3,
                                    "Quality": 1,
                                    "ExperimentalEffect": "special_powerdistributor_capacity",
                                    "Modifiers": [{
                                        "Label": "WeaponsCapacity",
                                        "Value": 39.8088,
                                        "OriginalValue": 38
                                    },
                                    {
                                        "Label": "WeaponsRecharge",
                                        "Value": 4.85394,
                                        "OriginalValue": 3.9
                                    },
                                    {
                                        "Label": "EnginesCapacity",
                                        "Value": 27.2376,
                                        "OriginalValue": 26
                                    },
                                    {
                                        "Label": "EnginesRecharge",
                                        "Value": 2.98704,
                                        "OriginalValue": 2.4
                                    },
                                    {
                                        "Label": "SystemsCapacity",
                                        "Value": 27.2376,
                                        "OriginalValue": 26
                                    },
                                    {
                                        "Label": "SystemsRecharge",
                                        "Value": 2.98704,
                                        "OriginalValue": 2.4
                                    }]
                                },
                                {
                                    "BlueprintName": "Sensor_LightWeight",
                                    "Level": 2,
                                    "Quality": 0.9593,
                                    "Modifiers": [{
                                        "Label": "Mass",
                                        "Value": 20.99536,
                                        "OriginalValue": 32
                                    },
                                    {
                                        "Label": "Integrity",
                                        "Value": 84,
                                        "OriginalValue": 105
                                    },
                                    {
                                        "Label": "SensorTargetScanAngle",
                                        "Value": 27,
                                        "OriginalValue": 30
                                    }]
                                }]
                            }
                        },
                        "examples": [{
                            "Slot": "CargoHatch",
                            "Item": "modularcargobaydoor",
                            "On": true,
                            "Priority": 0
                        },
                        {
                            "Slot": "TinyHardpoint6",
                            "Item": "hpt_heatsinklauncher_turret_tiny",
                            "On": true,
                            "Priority": 2,
                            "Value": 2520,
                            "Engineering": {
                                "BlueprintName": "Misc_HeatSinkCapacity",
                                "Level": 1,
                                "Quality": 1,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 2.6,
                                    "OriginalValue": 1.3
                                },
                                {
                                    "Label": "AmmoMaximum",
                                    "Value": 3,
                                    "OriginalValue": 2
                                },
                                {
                                    "Label": "ReloadTime",
                                    "Value": 15,
                                    "OriginalValue": 10
                                }]
                            }
                        },
                        {
                            "Slot": "TinyHardpoint7",
                            "Item": "hpt_heatsinklauncher_turret_tiny",
                            "On": true,
                            "Priority": 3,
                            "Value": 2520,
                            "Engineering": {
                                "BlueprintName": "Misc_HeatSinkCapacity",
                                "Level": 1,
                                "Quality": 1,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 2.6,
                                    "OriginalValue": 1.3
                                },
                                {
                                    "Label": "AmmoMaximum",
                                    "Value": 3,
                                    "OriginalValue": 2
                                },
                                {
                                    "Label": "ReloadTime",
                                    "Value": 15,
                                    "OriginalValue": 10
                                }]
                            }
                        },
                        {
                            "Slot": "TinyHardpoint8",
                            "Item": "hpt_heatsinklauncher_turret_tiny",
                            "On": true,
                            "Priority": 3,
                            "Value": 2520,
                            "Engineering": {
                                "BlueprintName": "Misc_HeatSinkCapacity",
                                "Level": 1,
                                "Quality": 1,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 2.6,
                                    "OriginalValue": 1.3
                                },
                                {
                                    "Label": "AmmoMaximum",
                                    "Value": 3,
                                    "OriginalValue": 2
                                },
                                {
                                    "Label": "ReloadTime",
                                    "Value": 15,
                                    "OriginalValue": 10
                                }]
                            }
                        },
                        {
                            "Slot": "Armour",
                            "Item": "cutter_armour_grade1",
                            "On": true,
                            "Priority": 1,
                            "Value": 0,
                            "Engineering": {
                                "BlueprintName": "Armour_HeavyDuty",
                                "Level": 5,
                                "Quality": 0.27,
                                "Modifiers": [{
                                    "Label": "DefenceModifierHealthMultiplier",
                                    "Value": 131.03,
                                    "OriginalValue": 80
                                },
                                {
                                    "Label": "KineticResistance",
                                    "Value":
                                        -
                                        14.876,
                                    "OriginalValue":
                                        -
                                        20
                                },
                                {
                                    "Label": "ThermicResistance",
                                    "Value": 4.27,
                                    "OriginalValue": 0
                                },
                                {
                                    "Label": "ExplosiveResistance",
                                    "Value":
                                        -
                                        34.022,
                                    "OriginalValue":
                                        -
                                        40
                                }]
                            }
                        },
                        {
                            "Slot": "PowerPlant",
                            "Item": "int_powerplant_size4_class5",
                            "On": true,
                            "Priority": 1,
                            "Value": 1037686,
                            "Engineering": {
                                "BlueprintName": "PowerPlant_Stealth",
                                "Level": 1,
                                "Quality": 1,
                                "ExperimentalEffect": "special_powerplant_lightweight",
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 4.68,
                                    "OriginalValue": 5
                                },
                                {
                                    "Label": "PowerCapacity",
                                    "Value": 15.132,
                                    "OriginalValue": 15.6
                                },
                                {
                                    "Label": "HeatEfficiency",
                                    "Value": 0.3,
                                    "OriginalValue": 0.4
                                }]
                            }
                        },
                        {
                            "Slot": "MainEngines",
                            "Item": "int_engine_size7_class2",
                            "On": true,
                            "Priority": 0,
                            "Value": 1367712,
                            "Engineering": {
                                "BlueprintName": "Engine_Tuned",
                                "Level": 5,
                                "Quality": 1,
                                "ExperimentalEffect": "special_engine_overloaded",
                                "Modifiers": [{
                                    "Label": "Integrity",
                                    "Value": 88.2,
                                    "OriginalValue": 105
                                },
                                {
                                    "Label": "PowerDraw",
                                    "Value": 7.9344,
                                    "OriginalValue": 6.84
                                },
                                {
                                    "Label": "EngineOptimalMass",
                                    "Value": 1458,
                                    "OriginalValue": 1620
                                },
                                {
                                    "Label": "EngineOptPerformance",
                                    "Value": 133.12,
                                    "OriginalValue": 100
                                },
                                {
                                    "Label": "EngineHeatRate",
                                    "Value": 0.572,
                                    "OriginalValue": 1.3
                                }]
                            }
                        },
                        {
                            "Slot": "FrameShiftDrive",
                            "Item": "int_hyperdrive_size7_class5",
                            "On": true,
                            "Priority": 2,
                            "Value": 36928159,
                            "Engineering": {
                                "BlueprintName": "FSD_LongRange",
                                "Level": 5,
                                "Quality": 0.934,
                                "ExperimentalEffect": "special_fsd_heavy",
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 104,
                                    "OriginalValue": 80
                                },
                                {
                                    "Label": "Integrity",
                                    "Value": 128.248,
                                    "OriginalValue": 164
                                },
                                {
                                    "Label": "PowerDraw",
                                    "Value": 1.035,
                                    "OriginalValue": 0.9
                                },
                                {
                                    "Label": "FSDOptimalMass",
                                    "Value": 4333.8672,
                                    "OriginalValue": 2700
                                }]
                            }
                        },
                        {
                            "Slot": "LifeSupport",
                            "Item": "int_lifesupport_size7_class2",
                            "On": true,
                            "Priority": 1,
                            "Value": 448445,
                            "Engineering": {
                                "BlueprintName": "Misc_LightWeight",
                                "Level": 2,
                                "Quality": 1,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 14.4,
                                    "OriginalValue": 32
                                },
                                {
                                    "Label": "Integrity",
                                    "Value": 94.4,
                                    "OriginalValue": 118
                                }]
                            }
                        },
                        {
                            "Slot": "PowerDistributor",
                            "Item": "int_powerdistributor_size6_class2",
                            "On": true,
                            "Priority": 2,
                            "Value": 160157,
                            "Engineering": {
                                "BlueprintName": "PowerDistributor_HighFrequency",
                                "Level": 3,
                                "Quality": 1,
                                "ExperimentalEffect": "special_powerdistributor_capacity",
                                "Modifiers": [{
                                    "Label": "WeaponsCapacity",
                                    "Value": 39.8088,
                                    "OriginalValue": 38
                                },
                                {
                                    "Label": "WeaponsRecharge",
                                    "Value": 4.85394,
                                    "OriginalValue": 3.9
                                },
                                {
                                    "Label": "EnginesCapacity",
                                    "Value": 27.2376,
                                    "OriginalValue": 26
                                },
                                {
                                    "Label": "EnginesRecharge",
                                    "Value": 2.98704,
                                    "OriginalValue": 2.4
                                },
                                {
                                    "Label": "SystemsCapacity",
                                    "Value": 27.2376,
                                    "OriginalValue": 26
                                },
                                {
                                    "Label": "SystemsRecharge",
                                    "Value": 2.98704,
                                    "OriginalValue": 2.4
                                }]
                            }
                        },
                        {
                            "Slot": "Radar",
                            "Item": "int_sensors_size7_class2",
                            "On": true,
                            "Priority": 1,
                            "Value": 448445,
                            "Engineering": {
                                "BlueprintName": "Sensor_LightWeight",
                                "Level": 2,
                                "Quality": 0.9593,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 20.99536,
                                    "OriginalValue": 32
                                },
                                {
                                    "Label": "Integrity",
                                    "Value": 84,
                                    "OriginalValue": 105
                                },
                                {
                                    "Label": "SensorTargetScanAngle",
                                    "Value": 27,
                                    "OriginalValue": 30
                                }]
                            }
                        },
                        {
                            "Slot": "FuelTank",
                            "Item": "int_fueltank_size6_class3",
                            "On": true,
                            "Priority": 1,
                            "Value": 245938
                        },
                        {
                            "Slot": "Slot01_Size8",
                            "Item": "int_cargorack_size8_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 2757506
                        },
                        {
                            "Slot": "Slot02_Size8",
                            "Item": "int_cargorack_size8_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 2757506
                        },
                        {
                            "Slot": "Slot03_Size6",
                            "Item": "int_cargorack_size6_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 261065
                        },
                        {
                            "Slot": "Slot04_Size6",
                            "Item": "int_cargorack_size6_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 261065
                        },
                        {
                            "Slot": "Slot05_Size6",
                            "Item": "int_cargorack_size6_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 261065
                        },
                        {
                            "Slot": "Slot06_Size5",
                            "Item": "int_fuelscoop_size5_class5",
                            "On": true,
                            "Priority": 0,
                            "Value": 6533057
                        },
                        {
                            "Slot": "Slot07_Size5",
                            "Item": "int_guardianfsdbooster_size5",
                            "On": true,
                            "Priority": 0,
                            "Value": 4667832
                        },
                        {
                            "Slot": "Slot08_Size4",
                            "Item": "int_corrosionproofcargorack_size4_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 67918
                        },
                        {
                            "Slot": "Slot09_Size3",
                            "Item": "int_supercruiseassist",
                            "On": true,
                            "Priority": 3,
                            "Value": 6566
                        },
                        {
                            "Slot": "Slot10_Size1",
                            "Item": "int_dockingcomputer_advanced",
                            "On": true,
                            "Priority": 3,
                            "Value": 9727
                        }]
                    },
                    "examples": [
                        [{
                            "Slot": "CargoHatch",
                            "Item": "modularcargobaydoor",
                            "On": true,
                            "Priority": 0
                        },
                        {
                            "Slot": "TinyHardpoint6",
                            "Item": "hpt_heatsinklauncher_turret_tiny",
                            "On": true,
                            "Priority": 2,
                            "Value": 2520,
                            "Engineering": {
                                "BlueprintName": "Misc_HeatSinkCapacity",
                                "Level": 1,
                                "Quality": 1,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 2.6,
                                    "OriginalValue": 1.3
                                },
                                {
                                    "Label": "AmmoMaximum",
                                    "Value": 3,
                                    "OriginalValue": 2
                                },
                                {
                                    "Label": "ReloadTime",
                                    "Value": 15,
                                    "OriginalValue": 10
                                }]
                            }
                        },
                        {
                            "Slot": "TinyHardpoint7",
                            "Item": "hpt_heatsinklauncher_turret_tiny",
                            "On": true,
                            "Priority": 3,
                            "Value": 2520,
                            "Engineering": {
                                "BlueprintName": "Misc_HeatSinkCapacity",
                                "Level": 1,
                                "Quality": 1,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 2.6,
                                    "OriginalValue": 1.3
                                },
                                {
                                    "Label": "AmmoMaximum",
                                    "Value": 3,
                                    "OriginalValue": 2
                                },
                                {
                                    "Label": "ReloadTime",
                                    "Value": 15,
                                    "OriginalValue": 10
                                }]
                            }
                        },
                        {
                            "Slot": "TinyHardpoint8",
                            "Item": "hpt_heatsinklauncher_turret_tiny",
                            "On": true,
                            "Priority": 3,
                            "Value": 2520,
                            "Engineering": {
                                "BlueprintName": "Misc_HeatSinkCapacity",
                                "Level": 1,
                                "Quality": 1,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 2.6,
                                    "OriginalValue": 1.3
                                },
                                {
                                    "Label": "AmmoMaximum",
                                    "Value": 3,
                                    "OriginalValue": 2
                                },
                                {
                                    "Label": "ReloadTime",
                                    "Value": 15,
                                    "OriginalValue": 10
                                }]
                            }
                        },
                        {
                            "Slot": "Armour",
                            "Item": "cutter_armour_grade1",
                            "On": true,
                            "Priority": 1,
                            "Value": 0,
                            "Engineering": {
                                "BlueprintName": "Armour_HeavyDuty",
                                "Level": 5,
                                "Quality": 0.27,
                                "Modifiers": [{
                                    "Label": "DefenceModifierHealthMultiplier",
                                    "Value": 131.03,
                                    "OriginalValue": 80
                                },
                                {
                                    "Label": "KineticResistance",
                                    "Value":
                                        -
                                        14.876,
                                    "OriginalValue":
                                        -
                                        20
                                },
                                {
                                    "Label": "ThermicResistance",
                                    "Value": 4.27,
                                    "OriginalValue": 0
                                },
                                {
                                    "Label": "ExplosiveResistance",
                                    "Value":
                                        -
                                        34.022,
                                    "OriginalValue":
                                        -
                                        40
                                }]
                            }
                        },
                        {
                            "Slot": "PowerPlant",
                            "Item": "int_powerplant_size4_class5",
                            "On": true,
                            "Priority": 1,
                            "Value": 1037686,
                            "Engineering": {
                                "BlueprintName": "PowerPlant_Stealth",
                                "Level": 1,
                                "Quality": 1,
                                "ExperimentalEffect": "special_powerplant_lightweight",
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 4.68,
                                    "OriginalValue": 5
                                },
                                {
                                    "Label": "PowerCapacity",
                                    "Value": 15.132,
                                    "OriginalValue": 15.6
                                },
                                {
                                    "Label": "HeatEfficiency",
                                    "Value": 0.3,
                                    "OriginalValue": 0.4
                                }]
                            }
                        },
                        {
                            "Slot": "MainEngines",
                            "Item": "int_engine_size7_class2",
                            "On": true,
                            "Priority": 0,
                            "Value": 1367712,
                            "Engineering": {
                                "BlueprintName": "Engine_Tuned",
                                "Level": 5,
                                "Quality": 1,
                                "ExperimentalEffect": "special_engine_overloaded",
                                "Modifiers": [{
                                    "Label": "Integrity",
                                    "Value": 88.2,
                                    "OriginalValue": 105
                                },
                                {
                                    "Label": "PowerDraw",
                                    "Value": 7.9344,
                                    "OriginalValue": 6.84
                                },
                                {
                                    "Label": "EngineOptimalMass",
                                    "Value": 1458,
                                    "OriginalValue": 1620
                                },
                                {
                                    "Label": "EngineOptPerformance",
                                    "Value": 133.12,
                                    "OriginalValue": 100
                                },
                                {
                                    "Label": "EngineHeatRate",
                                    "Value": 0.572,
                                    "OriginalValue": 1.3
                                }]
                            }
                        },
                        {
                            "Slot": "FrameShiftDrive",
                            "Item": "int_hyperdrive_size7_class5",
                            "On": true,
                            "Priority": 2,
                            "Value": 36928159,
                            "Engineering": {
                                "BlueprintName": "FSD_LongRange",
                                "Level": 5,
                                "Quality": 0.934,
                                "ExperimentalEffect": "special_fsd_heavy",
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 104,
                                    "OriginalValue": 80
                                },
                                {
                                    "Label": "Integrity",
                                    "Value": 128.248,
                                    "OriginalValue": 164
                                },
                                {
                                    "Label": "PowerDraw",
                                    "Value": 1.035,
                                    "OriginalValue": 0.9
                                },
                                {
                                    "Label": "FSDOptimalMass",
                                    "Value": 4333.8672,
                                    "OriginalValue": 2700
                                }]
                            }
                        },
                        {
                            "Slot": "LifeSupport",
                            "Item": "int_lifesupport_size7_class2",
                            "On": true,
                            "Priority": 1,
                            "Value": 448445,
                            "Engineering": {
                                "BlueprintName": "Misc_LightWeight",
                                "Level": 2,
                                "Quality": 1,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 14.4,
                                    "OriginalValue": 32
                                },
                                {
                                    "Label": "Integrity",
                                    "Value": 94.4,
                                    "OriginalValue": 118
                                }]
                            }
                        },
                        {
                            "Slot": "PowerDistributor",
                            "Item": "int_powerdistributor_size6_class2",
                            "On": true,
                            "Priority": 2,
                            "Value": 160157,
                            "Engineering": {
                                "BlueprintName": "PowerDistributor_HighFrequency",
                                "Level": 3,
                                "Quality": 1,
                                "ExperimentalEffect": "special_powerdistributor_capacity",
                                "Modifiers": [{
                                    "Label": "WeaponsCapacity",
                                    "Value": 39.8088,
                                    "OriginalValue": 38
                                },
                                {
                                    "Label": "WeaponsRecharge",
                                    "Value": 4.85394,
                                    "OriginalValue": 3.9
                                },
                                {
                                    "Label": "EnginesCapacity",
                                    "Value": 27.2376,
                                    "OriginalValue": 26
                                },
                                {
                                    "Label": "EnginesRecharge",
                                    "Value": 2.98704,
                                    "OriginalValue": 2.4
                                },
                                {
                                    "Label": "SystemsCapacity",
                                    "Value": 27.2376,
                                    "OriginalValue": 26
                                },
                                {
                                    "Label": "SystemsRecharge",
                                    "Value": 2.98704,
                                    "OriginalValue": 2.4
                                }]
                            }
                        },
                        {
                            "Slot": "Radar",
                            "Item": "int_sensors_size7_class2",
                            "On": true,
                            "Priority": 1,
                            "Value": 448445,
                            "Engineering": {
                                "BlueprintName": "Sensor_LightWeight",
                                "Level": 2,
                                "Quality": 0.9593,
                                "Modifiers": [{
                                    "Label": "Mass",
                                    "Value": 20.99536,
                                    "OriginalValue": 32
                                },
                                {
                                    "Label": "Integrity",
                                    "Value": 84,
                                    "OriginalValue": 105
                                },
                                {
                                    "Label": "SensorTargetScanAngle",
                                    "Value": 27,
                                    "OriginalValue": 30
                                }]
                            }
                        },
                        {
                            "Slot": "FuelTank",
                            "Item": "int_fueltank_size6_class3",
                            "On": true,
                            "Priority": 1,
                            "Value": 245938
                        },
                        {
                            "Slot": "Slot01_Size8",
                            "Item": "int_cargorack_size8_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 2757506
                        },
                        {
                            "Slot": "Slot02_Size8",
                            "Item": "int_cargorack_size8_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 2757506
                        },
                        {
                            "Slot": "Slot03_Size6",
                            "Item": "int_cargorack_size6_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 261065
                        },
                        {
                            "Slot": "Slot04_Size6",
                            "Item": "int_cargorack_size6_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 261065
                        },
                        {
                            "Slot": "Slot05_Size6",
                            "Item": "int_cargorack_size6_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 261065
                        },
                        {
                            "Slot": "Slot06_Size5",
                            "Item": "int_fuelscoop_size5_class5",
                            "On": true,
                            "Priority": 0,
                            "Value": 6533057
                        },
                        {
                            "Slot": "Slot07_Size5",
                            "Item": "int_guardianfsdbooster_size5",
                            "On": true,
                            "Priority": 0,
                            "Value": 4667832
                        },
                        {
                            "Slot": "Slot08_Size4",
                            "Item": "int_corrosionproofcargorack_size4_class1",
                            "On": true,
                            "Priority": 1,
                            "Value": 67918
                        },
                        {
                            "Slot": "Slot09_Size3",
                            "Item": "int_supercruiseassist",
                            "On": true,
                            "Priority": 3,
                            "Value": 6566
                        },
                        {
                            "Slot": "Slot10_Size1",
                            "Item": "int_dockingcomputer_advanced",
                            "On": true,
                            "Priority": 3,
                            "Value": 9727
                        }]
                    ]
                }
            },
            "examples": [{
                "event": "Loadout",
                "Ship": "cutter",
                "ShipName": "The Paper Machette",
                "ShipIdent": "JI-27C",
                "HullValue": 175924977,
                "ModulesValue": 58227409,
                "UnladenMass": 1301.17536,
                "CargoCapacity": 720,
                "MaxJumpRange": 52.128401,
                "FuelCapacity": {
                    "Main": 64,
                    "Reserve": 1.16
                },
                "Rebuy": 11707619,
                "Modules": [{
                    "Slot": "CargoHatch",
                    "Item": "modularcargobaydoor",
                    "On": true,
                    "Priority": 0
                },
                {
                    "Slot": "TinyHardpoint6",
                    "Item": "hpt_heatsinklauncher_turret_tiny",
                    "On": true,
                    "Priority": 2,
                    "Value": 2520,
                    "Engineering": {
                        "BlueprintName": "Misc_HeatSinkCapacity",
                        "Level": 1,
                        "Quality": 1,
                        "Modifiers": [{
                            "Label": "Mass",
                            "Value": 2.6,
                            "OriginalValue": 1.3
                        },
                        {
                            "Label": "AmmoMaximum",
                            "Value": 3,
                            "OriginalValue": 2
                        },
                        {
                            "Label": "ReloadTime",
                            "Value": 15,
                            "OriginalValue": 10
                        }]
                    }
                },
                {
                    "Slot": "TinyHardpoint7",
                    "Item": "hpt_heatsinklauncher_turret_tiny",
                    "On": true,
                    "Priority": 3,
                    "Value": 2520,
                    "Engineering": {
                        "BlueprintName": "Misc_HeatSinkCapacity",
                        "Level": 1,
                        "Quality": 1,
                        "Modifiers": [{
                            "Label": "Mass",
                            "Value": 2.6,
                            "OriginalValue": 1.3
                        },
                        {
                            "Label": "AmmoMaximum",
                            "Value": 3,
                            "OriginalValue": 2
                        },
                        {
                            "Label": "ReloadTime",
                            "Value": 15,
                            "OriginalValue": 10
                        }]
                    }
                },
                {
                    "Slot": "TinyHardpoint8",
                    "Item": "hpt_heatsinklauncher_turret_tiny",
                    "On": true,
                    "Priority": 3,
                    "Value": 2520,
                    "Engineering": {
                        "BlueprintName": "Misc_HeatSinkCapacity",
                        "Level": 1,
                        "Quality": 1,
                        "Modifiers": [{
                            "Label": "Mass",
                            "Value": 2.6,
                            "OriginalValue": 1.3
                        },
                        {
                            "Label": "AmmoMaximum",
                            "Value": 3,
                            "OriginalValue": 2
                        },
                        {
                            "Label": "ReloadTime",
                            "Value": 15,
                            "OriginalValue": 10
                        }]
                    }
                },
                {
                    "Slot": "Armour",
                    "Item": "cutter_armour_grade1",
                    "On": true,
                    "Priority": 1,
                    "Value": 0,
                    "Engineering": {
                        "BlueprintName": "Armour_HeavyDuty",
                        "Level": 5,
                        "Quality": 0.27,
                        "Modifiers": [{
                            "Label": "DefenceModifierHealthMultiplier",
                            "Value": 131.03,
                            "OriginalValue": 80
                        },
                        {
                            "Label": "KineticResistance",
                            "Value":
                                -
                                14.876,
                            "OriginalValue":
                                -
                                20
                        },
                        {
                            "Label": "ThermicResistance",
                            "Value": 4.27,
                            "OriginalValue": 0
                        },
                        {
                            "Label": "ExplosiveResistance",
                            "Value":
                                -
                                34.022,
                            "OriginalValue":
                                -
                                40
                        }]
                    }
                },
                {
                    "Slot": "PowerPlant",
                    "Item": "int_powerplant_size4_class5",
                    "On": true,
                    "Priority": 1,
                    "Value": 1037686,
                    "Engineering": {
                        "BlueprintName": "PowerPlant_Stealth",
                        "Level": 1,
                        "Quality": 1,
                        "ExperimentalEffect": "special_powerplant_lightweight",
                        "Modifiers": [{
                            "Label": "Mass",
                            "Value": 4.68,
                            "OriginalValue": 5
                        },
                        {
                            "Label": "PowerCapacity",
                            "Value": 15.132,
                            "OriginalValue": 15.6
                        },
                        {
                            "Label": "HeatEfficiency",
                            "Value": 0.3,
                            "OriginalValue": 0.4
                        }]
                    }
                },
                {
                    "Slot": "MainEngines",
                    "Item": "int_engine_size7_class2",
                    "On": true,
                    "Priority": 0,
                    "Value": 1367712,
                    "Engineering": {
                        "BlueprintName": "Engine_Tuned",
                        "Level": 5,
                        "Quality": 1,
                        "ExperimentalEffect": "special_engine_overloaded",
                        "Modifiers": [{
                            "Label": "Integrity",
                            "Value": 88.2,
                            "OriginalValue": 105
                        },
                        {
                            "Label": "PowerDraw",
                            "Value": 7.9344,
                            "OriginalValue": 6.84
                        },
                        {
                            "Label": "EngineOptimalMass",
                            "Value": 1458,
                            "OriginalValue": 1620
                        },
                        {
                            "Label": "EngineOptPerformance",
                            "Value": 133.12,
                            "OriginalValue": 100
                        },
                        {
                            "Label": "EngineHeatRate",
                            "Value": 0.572,
                            "OriginalValue": 1.3
                        }]
                    }
                },
                {
                    "Slot": "FrameShiftDrive",
                    "Item": "int_hyperdrive_size7_class5",
                    "On": true,
                    "Priority": 2,
                    "Value": 36928159,
                    "Engineering": {
                        "BlueprintName": "FSD_LongRange",
                        "Level": 5,
                        "Quality": 0.934,
                        "ExperimentalEffect": "special_fsd_heavy",
                        "Modifiers": [{
                            "Label": "Mass",
                            "Value": 104,
                            "OriginalValue": 80
                        },
                        {
                            "Label": "Integrity",
                            "Value": 128.248,
                            "OriginalValue": 164
                        },
                        {
                            "Label": "PowerDraw",
                            "Value": 1.035,
                            "OriginalValue": 0.9
                        },
                        {
                            "Label": "FSDOptimalMass",
                            "Value": 4333.8672,
                            "OriginalValue": 2700
                        }]
                    }
                },
                {
                    "Slot": "LifeSupport",
                    "Item": "int_lifesupport_size7_class2",
                    "On": true,
                    "Priority": 1,
                    "Value": 448445,
                    "Engineering": {
                        "BlueprintName": "Misc_LightWeight",
                        "Level": 2,
                        "Quality": 1,
                        "Modifiers": [{
                            "Label": "Mass",
                            "Value": 14.4,
                            "OriginalValue": 32
                        },
                        {
                            "Label": "Integrity",
                            "Value": 94.4,
                            "OriginalValue": 118
                        }]
                    }
                },
                {
                    "Slot": "PowerDistributor",
                    "Item": "int_powerdistributor_size6_class2",
                    "On": true,
                    "Priority": 2,
                    "Value": 160157,
                    "Engineering": {
                        "BlueprintName": "PowerDistributor_HighFrequency",
                        "Level": 3,
                        "Quality": 1,
                        "ExperimentalEffect": "special_powerdistributor_capacity",
                        "Modifiers": [{
                            "Label": "WeaponsCapacity",
                            "Value": 39.8088,
                            "OriginalValue": 38
                        },
                        {
                            "Label": "WeaponsRecharge",
                            "Value": 4.85394,
                            "OriginalValue": 3.9
                        },
                        {
                            "Label": "EnginesCapacity",
                            "Value": 27.2376,
                            "OriginalValue": 26
                        },
                        {
                            "Label": "EnginesRecharge",
                            "Value": 2.98704,
                            "OriginalValue": 2.4
                        },
                        {
                            "Label": "SystemsCapacity",
                            "Value": 27.2376,
                            "OriginalValue": 26
                        },
                        {
                            "Label": "SystemsRecharge",
                            "Value": 2.98704,
                            "OriginalValue": 2.4
                        }]
                    }
                },
                {
                    "Slot": "Radar",
                    "Item": "int_sensors_size7_class2",
                    "On": true,
                    "Priority": 1,
                    "Value": 448445,
                    "Engineering": {
                        "BlueprintName": "Sensor_LightWeight",
                        "Level": 2,
                        "Quality": 0.9593,
                        "Modifiers": [{
                            "Label": "Mass",
                            "Value": 20.99536,
                            "OriginalValue": 32
                        },
                        {
                            "Label": "Integrity",
                            "Value": 84,
                            "OriginalValue": 105
                        },
                        {
                            "Label": "SensorTargetScanAngle",
                            "Value": 27,
                            "OriginalValue": 30
                        }]
                    }
                },
                {
                    "Slot": "FuelTank",
                    "Item": "int_fueltank_size6_class3",
                    "On": true,
                    "Priority": 1,
                    "Value": 245938
                },
                {
                    "Slot": "Slot01_Size8",
                    "Item": "int_cargorack_size8_class1",
                    "On": true,
                    "Priority": 1,
                    "Value": 2757506
                },
                {
                    "Slot": "Slot02_Size8",
                    "Item": "int_cargorack_size8_class1",
                    "On": true,
                    "Priority": 1,
                    "Value": 2757506
                },
                {
                    "Slot": "Slot03_Size6",
                    "Item": "int_cargorack_size6_class1",
                    "On": true,
                    "Priority": 1,
                    "Value": 261065
                },
                {
                    "Slot": "Slot04_Size6",
                    "Item": "int_cargorack_size6_class1",
                    "On": true,
                    "Priority": 1,
                    "Value": 261065
                },
                {
                    "Slot": "Slot05_Size6",
                    "Item": "int_cargorack_size6_class1",
                    "On": true,
                    "Priority": 1,
                    "Value": 261065
                },
                {
                    "Slot": "Slot06_Size5",
                    "Item": "int_fuelscoop_size5_class5",
                    "On": true,
                    "Priority": 0,
                    "Value": 6533057
                },
                {
                    "Slot": "Slot07_Size5",
                    "Item": "int_guardianfsdbooster_size5",
                    "On": true,
                    "Priority": 0,
                    "Value": 4667832
                },
                {
                    "Slot": "Slot08_Size4",
                    "Item": "int_corrosionproofcargorack_size4_class1",
                    "On": true,
                    "Priority": 1,
                    "Value": 67918
                },
                {
                    "Slot": "Slot09_Size3",
                    "Item": "int_supercruiseassist",
                    "On": true,
                    "Priority": 3,
                    "Value": 6566
                },
                {
                    "Slot": "Slot10_Size1",
                    "Item": "int_dockingcomputer_advanced",
                    "On": true,
                    "Priority": 3,
                    "Value": 9727
                }]
            }]
        }
    },
    "examples": [{
        "header": {
            "appName": "EDSY",
            "appVersion": 308149912,
            "appURL": "https://edsy.org/#/L=H_5XfyPl0H4C0S00,,mpXCjwIK2G_W0CjwJK2G_W0CjwJK2G_W0,9p3H05GGu0A72HK4J_W0ARqGK3I_W0Ag_IK5JwO0Ax4HK2W_W0B98IK2n_W0BQKHK2Wxz0BeE1K,,0DI1K0DI1K0AA1K0AA1K0AA1K50U0K34a0K0731K0nG3K0nF3K,The_0Paper_0Machette,JI_D27C"
        },
        "data": {
            "event": "Loadout",
            "Ship": "cutter",
            "ShipName": "The Paper Machette",
            "ShipIdent": "JI-27C",
            "HullValue": 175924977,
            "ModulesValue": 58227409,
            "UnladenMass": 1301.17536,
            "CargoCapacity": 720,
            "MaxJumpRange": 52.128401,
            "FuelCapacity": {
                "Main": 64,
                "Reserve": 1.16
            },
            "Rebuy": 11707619,
            "Modules": [{
                "Slot": "CargoHatch",
                "Item": "modularcargobaydoor",
                "On": true,
                "Priority": 0
            },
            {
                "Slot": "TinyHardpoint6",
                "Item": "hpt_heatsinklauncher_turret_tiny",
                "On": true,
                "Priority": 2,
                "Value": 2520,
                "Engineering": {
                    "BlueprintName": "Misc_HeatSinkCapacity",
                    "Level": 1,
                    "Quality": 1,
                    "Modifiers": [{
                        "Label": "Mass",
                        "Value": 2.6,
                        "OriginalValue": 1.3
                    },
                    {
                        "Label": "AmmoMaximum",
                        "Value": 3,
                        "OriginalValue": 2
                    },
                    {
                        "Label": "ReloadTime",
                        "Value": 15,
                        "OriginalValue": 10
                    }]
                }
            },
            {
                "Slot": "TinyHardpoint7",
                "Item": "hpt_heatsinklauncher_turret_tiny",
                "On": true,
                "Priority": 3,
                "Value": 2520,
                "Engineering": {
                    "BlueprintName": "Misc_HeatSinkCapacity",
                    "Level": 1,
                    "Quality": 1,
                    "Modifiers": [{
                        "Label": "Mass",
                        "Value": 2.6,
                        "OriginalValue": 1.3
                    },
                    {
                        "Label": "AmmoMaximum",
                        "Value": 3,
                        "OriginalValue": 2
                    },
                    {
                        "Label": "ReloadTime",
                        "Value": 15,
                        "OriginalValue": 10
                    }]
                }
            },
            {
                "Slot": "TinyHardpoint8",
                "Item": "hpt_heatsinklauncher_turret_tiny",
                "On": true,
                "Priority": 3,
                "Value": 2520,
                "Engineering": {
                    "BlueprintName": "Misc_HeatSinkCapacity",
                    "Level": 1,
                    "Quality": 1,
                    "Modifiers": [{
                        "Label": "Mass",
                        "Value": 2.6,
                        "OriginalValue": 1.3
                    },
                    {
                        "Label": "AmmoMaximum",
                        "Value": 3,
                        "OriginalValue": 2
                    },
                    {
                        "Label": "ReloadTime",
                        "Value": 15,
                        "OriginalValue": 10
                    }]
                }
            },
            {
                "Slot": "Armour",
                "Item": "cutter_armour_grade1",
                "On": true,
                "Priority": 1,
                "Value": 0,
                "Engineering": {
                    "BlueprintName": "Armour_HeavyDuty",
                    "Level": 5,
                    "Quality": 0.27,
                    "Modifiers": [{
                        "Label": "DefenceModifierHealthMultiplier",
                        "Value": 131.03,
                        "OriginalValue": 80
                    },
                    {
                        "Label": "KineticResistance",
                        "Value":
                            -
                            14.876,
                        "OriginalValue":
                            -
                            20
                    },
                    {
                        "Label": "ThermicResistance",
                        "Value": 4.27,
                        "OriginalValue": 0
                    },
                    {
                        "Label": "ExplosiveResistance",
                        "Value":
                            -
                            34.022,
                        "OriginalValue":
                            -
                            40
                    }]
                }
            },
            {
                "Slot": "PowerPlant",
                "Item": "int_powerplant_size4_class5",
                "On": true,
                "Priority": 1,
                "Value": 1037686,
                "Engineering": {
                    "BlueprintName": "PowerPlant_Stealth",
                    "Level": 1,
                    "Quality": 1,
                    "ExperimentalEffect": "special_powerplant_lightweight",
                    "Modifiers": [{
                        "Label": "Mass",
                        "Value": 4.68,
                        "OriginalValue": 5
                    },
                    {
                        "Label": "PowerCapacity",
                        "Value": 15.132,
                        "OriginalValue": 15.6
                    },
                    {
                        "Label": "HeatEfficiency",
                        "Value": 0.3,
                        "OriginalValue": 0.4
                    }]
                }
            },
            {
                "Slot": "MainEngines",
                "Item": "int_engine_size7_class2",
                "On": true,
                "Priority": 0,
                "Value": 1367712,
                "Engineering": {
                    "BlueprintName": "Engine_Tuned",
                    "Level": 5,
                    "Quality": 1,
                    "ExperimentalEffect": "special_engine_overloaded",
                    "Modifiers": [{
                        "Label": "Integrity",
                        "Value": 88.2,
                        "OriginalValue": 105
                    },
                    {
                        "Label": "PowerDraw",
                        "Value": 7.9344,
                        "OriginalValue": 6.84
                    },
                    {
                        "Label": "EngineOptimalMass",
                        "Value": 1458,
                        "OriginalValue": 1620
                    },
                    {
                        "Label": "EngineOptPerformance",
                        "Value": 133.12,
                        "OriginalValue": 100
                    },
                    {
                        "Label": "EngineHeatRate",
                        "Value": 0.572,
                        "OriginalValue": 1.3
                    }]
                }
            },
            {
                "Slot": "FrameShiftDrive",
                "Item": "int_hyperdrive_size7_class5",
                "On": true,
                "Priority": 2,
                "Value": 36928159,
                "Engineering": {
                    "BlueprintName": "FSD_LongRange",
                    "Level": 5,
                    "Quality": 0.934,
                    "ExperimentalEffect": "special_fsd_heavy",
                    "Modifiers": [{
                        "Label": "Mass",
                        "Value": 104,
                        "OriginalValue": 80
                    },
                    {
                        "Label": "Integrity",
                        "Value": 128.248,
                        "OriginalValue": 164
                    },
                    {
                        "Label": "PowerDraw",
                        "Value": 1.035,
                        "OriginalValue": 0.9
                    },
                    {
                        "Label": "FSDOptimalMass",
                        "Value": 4333.8672,
                        "OriginalValue": 2700
                    }]
                }
            },
            {
                "Slot": "LifeSupport",
                "Item": "int_lifesupport_size7_class2",
                "On": true,
                "Priority": 1,
                "Value": 448445,
                "Engineering": {
                    "BlueprintName": "Misc_LightWeight",
                    "Level": 2,
                    "Quality": 1,
                    "Modifiers": [{
                        "Label": "Mass",
                        "Value": 14.4,
                        "OriginalValue": 32
                    },
                    {
                        "Label": "Integrity",
                        "Value": 94.4,
                        "OriginalValue": 118
                    }]
                }
            },
            {
                "Slot": "PowerDistributor",
                "Item": "int_powerdistributor_size6_class2",
                "On": true,
                "Priority": 2,
                "Value": 160157,
                "Engineering": {
                    "BlueprintName": "PowerDistributor_HighFrequency",
                    "Level": 3,
                    "Quality": 1,
                    "ExperimentalEffect": "special_powerdistributor_capacity",
                    "Modifiers": [{
                        "Label": "WeaponsCapacity",
                        "Value": 39.8088,
                        "OriginalValue": 38
                    },
                    {
                        "Label": "WeaponsRecharge",
                        "Value": 4.85394,
                        "OriginalValue": 3.9
                    },
                    {
                        "Label": "EnginesCapacity",
                        "Value": 27.2376,
                        "OriginalValue": 26
                    },
                    {
                        "Label": "EnginesRecharge",
                        "Value": 2.98704,
                        "OriginalValue": 2.4
                    },
                    {
                        "Label": "SystemsCapacity",
                        "Value": 27.2376,
                        "OriginalValue": 26
                    },
                    {
                        "Label": "SystemsRecharge",
                        "Value": 2.98704,
                        "OriginalValue": 2.4
                    }]
                }
            },
            {
                "Slot": "Radar",
                "Item": "int_sensors_size7_class2",
                "On": true,
                "Priority": 1,
                "Value": 448445,
                "Engineering": {
                    "BlueprintName": "Sensor_LightWeight",
                    "Level": 2,
                    "Quality": 0.9593,
                    "Modifiers": [{
                        "Label": "Mass",
                        "Value": 20.99536,
                        "OriginalValue": 32
                    },
                    {
                        "Label": "Integrity",
                        "Value": 84,
                        "OriginalValue": 105
                    },
                    {
                        "Label": "SensorTargetScanAngle",
                        "Value": 27,
                        "OriginalValue": 30
                    }]
                }
            },
            {
                "Slot": "FuelTank",
                "Item": "int_fueltank_size6_class3",
                "On": true,
                "Priority": 1,
                "Value": 245938
            },
            {
                "Slot": "Slot01_Size8",
                "Item": "int_cargorack_size8_class1",
                "On": true,
                "Priority": 1,
                "Value": 2757506
            },
            {
                "Slot": "Slot02_Size8",
                "Item": "int_cargorack_size8_class1",
                "On": true,
                "Priority": 1,
                "Value": 2757506
            },
            {
                "Slot": "Slot03_Size6",
                "Item": "int_cargorack_size6_class1",
                "On": true,
                "Priority": 1,
                "Value": 261065
            },
            {
                "Slot": "Slot04_Size6",
                "Item": "int_cargorack_size6_class1",
                "On": true,
                "Priority": 1,
                "Value": 261065
            },
            {
                "Slot": "Slot05_Size6",
                "Item": "int_cargorack_size6_class1",
                "On": true,
                "Priority": 1,
                "Value": 261065
            },
            {
                "Slot": "Slot06_Size5",
                "Item": "int_fuelscoop_size5_class5",
                "On": true,
                "Priority": 0,
                "Value": 6533057
            },
            {
                "Slot": "Slot07_Size5",
                "Item": "int_guardianfsdbooster_size5",
                "On": true,
                "Priority": 0,
                "Value": 4667832
            },
            {
                "Slot": "Slot08_Size4",
                "Item": "int_corrosionproofcargorack_size4_class1",
                "On": true,
                "Priority": 1,
                "Value": 67918
            },
            {
                "Slot": "Slot09_Size3",
                "Item": "int_supercruiseassist",
                "On": true,
                "Priority": 3,
                "Value": 6566
            },
            {
                "Slot": "Slot10_Size1",
                "Item": "int_dockingcomputer_advanced",
                "On": true,
                "Priority": 3,
                "Value": 9727
            }]
        }
    }]
}
```

below:

kotlin pojo data class tracking SLEF Data, FrameShiftDrive, and GuardianFSDBooster from the schema elements
*/

@Serializable
class SLEFData(
    @SerialName("event")
    val event: String, // SLEF
    @SerialName("Ship")
    val ship: String, // type-9
    @SerialName("ShipID")
    val shipID: Int, // 1
    @SerialName("Items")
    val items: List<Item>,
    @SerialName("Components")
    val components: List<Component>
) {
    @Serializable
    class Item(
        @SerialName("Slot")
        val slot: String, // SLEF
        @SerialName("Item")
        val item: String, // type-9
        @SerialName("On")
        val on: Boolean, // true
        @SerialName("Priority")
        val priority: Int, // 1
        @SerialName("Value")
        val value: Int // 1
    )

    @Serializable
    class Component(
        @SerialName("Slot")
        val slot: String, // SLEF
        @SerialName("Item")
        val item: String, // type-9
        @SerialName("On")
        val on: Boolean, // true
        @SerialName("Priority")
        val priority: Int, // 1
        @SerialName("Value")
        val value: Int, // 1
        @SerialName("Engineering")
        val engineering: Engineering? = null
    ) {
        @Serializable
        class Engineering(
            @SerialName("BlueprintName")
            val blueprintName: String, // SLEF
            @SerialName("Level")
            val level: Int, // 1
            @SerialName("Quality")
            val quality: Double, // 1
            @SerialName("Modifiers")
            val modifiers: List<Modifier>? = null,
            @SerialName("ExperimentalEffect")
            val experimentalEffect: String? = null
        ) {
            @Serializable
            class Modifier(
                @SerialName("Label")
                val label: String, // SLEF
                @SerialName("Value")
                val value: Double, // 1
                @SerialName("OriginalValue")
                val originalValue: Double // 1
            )
        }
    }
}

//which slot has an FSD ?