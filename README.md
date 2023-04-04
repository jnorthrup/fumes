# Fumes v1

Elite Dangerous non-refuelling route plotter

what if you could pilot an all-out engineered combat ship in Elite Dangerous further than your class 4 tank will
normally take you in under 30 jumps?
Bring along every module crucial to the destination going in hot-- and the best part is you can skip the distractions at
carriers and stations before and after?

Coming in Spring 2023 to theaters everywhere, the EDMC plugin absolutely noone is asking for... Fumes!

## galaxy.gz ingest

on my pc built in tmpfs (32GiB RAM, 12P+4E cores , 5/3.5GHz ) it takes 38 minutes to ingest the 65GiB galaxy.json.gz
file:

`read 336.63 GiB in 38m 55.825937802s -- avg 39.91 KiLOC/json , 147.62 MiB  per second; avgline size 3.69 KiB`

the working plan is to import the daily, and the full galaxy.json.gz files, and then prioritize based on smaller size

daemon updates would ping the server for HEAD digests and dispatch when something is new.S

### indexes

#### as btrfs compression

I checked this on my btrfs compressed volume mounted as `(rw,noatime,compress=lzo,ssd,discard=async,space_cache=v2)`

the isam indexes are apparently btrfs lzo friendly. compsize says:

| Type  | Perc | Disk Usage | Uncompressed | Referenced |
|-------|-----:|-----------:|-------------:|-----------:|
| TOTAL |  41% |       3.5G |         8.4G |       8.4G |      
| none  | 100% |       1.3G |         1.3G |       1.3G |      
| lzo   |  30% |       2.2G |         7.1G |       7.1G |      

#### raw

* total bytes for gzip cache:  65G

* total index bytes: 8.5G

| size | file                            |
|------|---------------------------------|
| 729M | galaxy.json.Id64.isam           |
| 117  | galaxy.json.Id64.isam.meta      |
| 5.7G | galaxy.json.Name.isam           |
| 123  | galaxy.json.Name.isam.meta      |
| 729M | galaxy.json.Seek.isam           |
| 117  | galaxy.json.Seek.isam.meta      |
| 365M | galaxy.json.X.isam              |
| 112  | galaxy.json.X.isam.meta         |
| 365M | galaxy.json.Y.isam              |
| 112  | galaxy.json.Y.isam.meta         |
| 365M | galaxy.json.Z.isam              |
| 112  | galaxy.json.Z.isam.meta         |
| 65G  | galaxy.json.gz                  |
| 240M | galaxy.json.gzi                 |
| 707K | galaxy_1day.json.Id64.isam      |
| 117  | galaxy_1day.json.Id64.isam.meta |
| 5.6M | galaxy_1day.json.Name.isam      |
| 123  | galaxy_1day.json.Name.isam.meta |
| 709K | galaxy_1day.json.Seek.isam      |
| 117  | galaxy_1day.json.Seek.isam.meta |
| 355K | galaxy_1day.json.X.isam         |
| 112  | galaxy_1day.json.X.isam.meta    |
| 355K | galaxy_1day.json.Y.isam         |
| 112  | galaxy_1day.json.Y.isam.meta    |
| 355K | galaxy_1day.json.Z.isam         |
| 112  | galaxy_1day.json.Z.isam.meta    |
| 563M | galaxy_1day.json.gz             |
| 2.0M | galaxy_1day.json.gzi            |

# Roadmap v2+

# Elite Dangerous Game Support Tool Architecture

## Overview

This architecture is designed to support a game support tool for Elite Dangerous, which will utilize a gossip network,
IPFS nodes, and Kotlin Common codebase.

At the heart of it all is a goal, something like $5 monthly cloud hosting, which easily extends to broadband
contributors.  
The tenets seem simple, sometimes source data is hosted on the net, or dumps like Spansh,
and sometimes these need hosting, like wikis, or fresh EDDN updates, or any span of usecases in between.

## Architecture

https://github.com/jnorthrup/GardenKollektion represents a career's worth of distillations and experience in
mutli-agent distributed architecture and how the internet has evolved what works gradually over time as a tug of war
between centralization hardening and decentralization survivability. Roughly speaking, a DHT with Kademlia
architecture can be reused as an actor work-sharing network on gossip nodes. for a 64 bit DHT network, jobs may kick off
to nominate a small network of 3,7, or 21 nodes to do some kind of shared work or election, and publish a result to IPFS
journals. Grossly oversimplified but not impossible.

## Services

### EDDN Subnet

This service will cycle through a subnet of EDDN bus nodes for larger network consumption, perform tiered aggregation
for outer networks, and submit signals into the ZeroMQ datasphere.

#### Potential Platforms and Tools:

- Node.js IPFS node
- ZeroMQ library
- Kotlin Common

#### Kotlin Compilation Target:

- Node.js

### Ephemeral Storage Caching

This service will cache indexes of the Spansh gzip archive converted to gzindex format, allowing for random access to
gzip contents.

#### Potential Platforms and Tools:

- IPFS nodes
- Kotlin Common

#### Kotlin Compilation Target:

- JVM

### Large Data File Management

Using Spansh dumps as a perfect a p2p network could host shards of a 95 GB average gzip posted weekly. Random access
index is a cool gotta-havit distraction, sort of, where a use-contribution of bandwidth and a few megs of storage could
form the basis of a key value store and route/market/BGS caching access

#### Potential Platforms and Tools:

- IPFS nodes
- Kotlin Common

#### Kotlin Compilation Target:

- JVM

### Gossip Network

This service will enable nodes to share information and elect a topology entity as a resource among several to perform
work, and make a quorum.

#### Potential Platforms and Tools:

- ZeroMQ library
- Kotlin Common

#### Kotlin Compilation Target:

- JVM

## IPFS Integration

IPFS will be a crucial part of this architecture, and will be used to store and retrieve large data files, as well as to
interact with IPFS nodes via a Node.js IPFS node.

### Potential IPFS Integrations:

- IPFS API
- IPLD
- libp2p

#### Kotlin Compilation Target:

- JVM

## Conclusion

By utilizing Kotlin Common and IPFS, this architecture will allow for a game support tool for Elite Dangerous that is
scalable, reliable, and efficient.
