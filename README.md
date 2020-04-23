## World generation process

### 1. makeBase
In the normal generator, this create a base of stone with varying heights using the OctavesNoiseGenerator. We use it to create stone hemispheres, connected by bridges with fence blocks. Then water is added for any blocks below the sea level.

### 2. buildSurface
This goes over the stone, changing it from stone to the appropriate surface block (dirt, gravel, terrocotta etc.) at varying depths. We don't do anything special here, other than make sure it doesn't run outside of the sphere.

### 3. carve
This creates any caves ravines, and under-water lava lakes. We don't anything special other than make sure it doesn't create anything outside of the spheres. Since it works on chunks, rather than blocks, we don't have any control on a block-by-block basis, so we wrap the chunk object which will then ignore any requests to change blocks outside of the spheres.

### 4. decorate
This adds trees, flowers, chests, and icebergs. Since this works on an even bigger scale than carving, we generate these as normal, then go round and delete anything that falls outside of the spheres.