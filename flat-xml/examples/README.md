Flat-XML Examples
=================

```xml
<?xml version="1.0"?>
<cat>
<variety>Nebelung</variety>
<name><first>Francis</first><middle>Greyscale</middle><last>Lux</last></name>
<origin type="informal">San Francisco SPCA</origin>
</cat>
```

```shell
$ flat-xml greyscale.xml
/?xml {
/?xml/@version {
/?xml/@version - 1.0
/?xml/@version }
/?xml }
/cat {
/cat +
/cat -
/cat/variety {
/cat/variety - Nebelung
/cat/variety }
/cat +
/cat -
/cat/name {
/cat/name/first {
/cat/name/first - Francis
/cat/name/first }
/cat/name/middle {
/cat/name/middle - Greyscale
/cat/name/middle }
/cat/name/last {
/cat/name/last - Lux
/cat/name/last }
/cat/name }
/cat +
/cat -
/cat/origin {
/cat/origin/@type {
/cat/origin/@type - informal
/cat/origin/@type }
/cat/origin - San Francisco SPCA
/cat/origin }
/cat +
/cat -
/cat }
```

Note that `flat-xml` does not ignore any whitespace, which is what the
multiple `+`-then-`-` lines are all about.

```xml
<!-- One of my favorites. -->
<poem>A peanut sat upon a track.
Its heart was all a-flutter.
A train came speeding down that track.
Toot! Toot! Peanut butter.

    -- Ogden Nash</poem>
```

```shell
$ flat-xml peanut.xml
/poem {
/poem + A peanut sat upon a track.
/poem + Its heart was all a-flutter.
/poem + A train came speeding down that track.
/poem + Toot! Toot! Peanut butter.
/poem +
/poem -     -- Ogden Nash
/poem }
```
