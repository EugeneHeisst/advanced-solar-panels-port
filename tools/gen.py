#!/usr/bin/env python3
"""Original ASP assets and modern recipes. Run before building; no original binary classes are loaded."""
import json,re,shutil
from pathlib import Path
ROOT=Path(__file__).resolve().parent.parent
SRC=ROOT/'related/asp-reference/jar/assets/advanced_solar_panels'
RES=ROOT/'src/generated/resources'; A=RES/'assets/advanced_solar_panels'; D=RES/'data/advanced_solar_panels'
def write(path,obj):
 path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(obj,ensure_ascii=False,indent=2)+'\n')
def copy(src,dst):
 dst.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(src,dst)
items=['sunnarium','sunnarium_part','sunnarium_alloy','irradiant_uranium','enriched_sunnarium','enriched_sunnarium_alloy','irradiant_glass_pane','iridium_iron_plate','reinforced_iridium_iron_plate','irradiant_reinforced_plate','iridium_ingot','uranium_ingot','mt_core','quantum_core']
blocks=['advanced_solar_panel','hybrid_solar_panel','ultimate_solar_panel','quantum_solar_panel','quantum_generator','molecular_transformer']
for f in (SRC/'textures').rglob('*.png'):
 relative=f.relative_to(SRC/'textures').as_posix().replace(' ','_').lower()
 if relative.startswith('blocks/'):relative='block/'+relative[7:]
 if relative.startswith('items/'):relative='item/'+relative[6:]
 dst=A/'textures'/relative;copy(f,dst)
for iid in items:
 tex=iid.replace('_','');write(A/'models/item'/f'{iid}.json',{'parent':'minecraft:item/generated','textures':{'layer0':f'advanced_solar_panels:item/{tex}'}})
for name in ['advanced','hybrid','ultimate']:
 iid=name+'_solar_helmet';tex='advsolarhelmet' if name=='advanced' else name+'solarhelmet';textures={'layer0':'advanced_solar_panels:item/'+tex}
 if name!='advanced':textures['layer1']=textures['layer0']+'overlay'
 write(A/'models/item'/f'{iid}.json',{'parent':'minecraft:item/generated','textures':textures})
 for layer in [1,2]:
  copy(SRC/'textures/armour'/f'{name}solarhelmet.png',A/'textures/models/armor'/f'{name}_solar_layer_{layer}.png')
  if name!='advanced':copy(SRC/'textures/armour'/f'{name}solarhelmetoverlay.png',A/'textures/models/armor'/f'{name}_solar_layer_{layer}_overlay.png')
write(A/'models/item/double_stone_slab.json',{'parent':'minecraft:block/smooth_stone_slab_double'})
for bid in blocks:
 if 'solar_panel' in bid:
  level=bid.split('_')[0];tex={f:'advanced_solar_panels:block/'+level+'_solar_'+('top' if f=='up' else 'bottom' if f=='down' and level!='quantum' else 'side') for f in ['down','up','north','south','east','west']};tex['particle']=tex['north'];model={'parent':'minecraft:block/cube','textures':tex}
 elif bid=='quantum_generator':model={'parent':'minecraft:block/cube_all','textures':{'all':'advanced_solar_panels:block/quantum_generator'}}
 else:model={'parent':'minecraft:block/cube_all','textures':{'all':'advanced_solar_panels:block/advanced_solar_side'}}
 write(A/'models/block'/f'{bid}.json',model)
 variants={'':{'model':f'advanced_solar_panels:block/{bid}'}}
 if bid=='quantum_generator':
  write(A/'models/block/quantum_generator_active.json',{'parent':'minecraft:block/cube_all','textures':{'all':'advanced_solar_panels:block/quantum_generator_active'}});variants={'active=false':{'model':'advanced_solar_panels:block/quantum_generator'},'active=true':{'model':'advanced_solar_panels:block/quantum_generator_active'}}
 write(A/'blockstates'/f'{bid}.json',{'variants':variants});write(A/'models/item'/f'{bid}.json',{'parent':f'advanced_solar_panels:block/{bid}'})
 write(D/'loot_table/blocks'/f'{bid}.json',{'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':f'advanced_solar_panels:{bid}'}],'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
for language,legacy in [('en_us','en_US'),('ru_ru','ru_RU')]:
 data={}
 for line in (SRC/'lang'/f'{legacy}.lang').read_text().splitlines():
  if '=' in line and not line.startswith('#'):
   k,v=line.split('=',1);data[k]=v
 for bid in blocks:data['block.advanced_solar_panels.'+bid]=data['advanced_solar_panels.machines.'+bid]
 for iid in items:data['item.advanced_solar_panels.'+iid]=data['advanced_solar_panels.crafting.'+iid]
 for name in ['advanced','hybrid','ultimate']:data[f'item.advanced_solar_panels.{name}_solar_helmet']=data['advanced_solar_panels.solar_helmets.'+name]
 data['item.advanced_solar_panels.double_stone_slab']='Двойная каменная плита' if language=='ru_ru' else 'Double Stone Slab';data['itemGroup.advanced_solar_panels']='Advanced Solar Panels';data['advanced_solar_panels.gui.output']=data.get('advanced_solar_panels.gui.output','Output:')
 for n,mode in enumerate(['Disabled','Basic','Extended','Advanced']):data['advanced_solar_panels.hud.mode.'+str(n)]='HUD: '+mode
 write(A/'lang'/f'{language}.json',data)
for metal in ['iridium','uranium']:
 write(RES/'data/c/tags/item/ingots'/f'{metal}.json',{'replace':False,'values':[f'advanced_solar_panels:{metal}_ingot']})
write(RES/'data/minecraft/tags/item/dyeable.json',{'replace':False,'values':['advanced_solar_panels:hybrid_solar_helmet','advanced_solar_panels:ultimate_solar_helmet']})
write(RES/'data/minecraft/tags/block/mineable/pickaxe.json',{'replace':False,'values':['advanced_solar_panels:'+b for b in blocks]})
def ingredient(s):return {'tag':s[1:]} if s.startswith('#') else {'item':s if ':' in s else 'advanced_solar_panels:'+s}
def shaped(out,pattern,keys,count=1,suffix=''):
 write(D/'recipe'/(out+suffix+'.json'),{'type':'ic2:shaped','pattern':pattern.split('|'),'key':{k:ingredient(v) for k,v in keys.items()},'result':{'id':'advanced_solar_panels:'+out,'count':count}})
def shapeless(out,inputs,count=1,suffix='_shapeless'):
 write(D/'recipe'/(out+suffix+'.json'),{'type':'ic2:shapeless','ingredients':[ingredient(s) for s in inputs],'result':{'id':'advanced_solar_panels:'+out,'count':count}})
shaped('iridium_iron_plate','III|IPI|III',{'I':'#c:plates/iron','P':'#c:ingots/iridium'})
shaped('reinforced_iridium_iron_plate','ACA|CIC|ACA',{'A':'ic2:advanced_alloy','C':'ic2:carbon_plate','I':'iridium_iron_plate'})
shaped('irradiant_reinforced_plate','RSR|LIL|RDR',{'R':'minecraft:redstone','S':'sunnarium_part','L':'minecraft:lapis_lazuli','I':'reinforced_iridium_iron_plate','D':'minecraft:diamond'})
shaped('advanced_solar_panel','PPP|ASA|CIC',{'P':'irradiant_glass_pane','A':'ic2:advanced_alloy','S':'ic2:solar_panel','C':'ic2:advanced_circuit','I':'irradiant_reinforced_plate'})
shaped('hybrid_solar_panel','PLP|IAI|CSC',{'P':'ic2:carbon_plate','L':'minecraft:lapis_block','I':'ic2:iridium_plate','A':'advanced_solar_panel','C':'ic2:advanced_circuit','S':'enriched_sunnarium'})
shaped('ultimate_solar_panel',' L |CSC|ECE',{'L':'minecraft:lapis_block','C':'ic2:coal_chunk','S':'advanced_solar_panel','E':'enriched_sunnarium_alloy'})
shaped('ultimate_solar_panel','SSS|SCS|SSS',{'S':'hybrid_solar_panel','C':'ic2:advanced_circuit'},suffix='_alternate')
shapeless('hybrid_solar_panel',['ultimate_solar_panel'],8)
shaped('quantum_core','ANA|NEN|ANA',{'A':'enriched_sunnarium_alloy','N':'minecraft:nether_star','E':'minecraft:ender_eye'})
shaped('quantum_solar_panel','SSS|SQS|SSS',{'S':'ultimate_solar_panel','Q':'quantum_core'})
for name,panel,helmet,wire,transformer in [('advanced','advanced','nano','insulated_gold','lv'),('hybrid','hybrid','quantum','glass_fibre','hv'),('ultimate','ultimate','quantum','glass_fibre','hv')]:shaped(name+'_solar_helmet',' S |CQC|GTG',{'S':panel+'_solar_panel','C':'ic2:advanced_circuit','Q':'ic2:'+helmet+'_helmet','G':'ic2:'+wire+'_cable','T':'ic2:'+transformer+'_transformer'})
shapeless('ultimate_solar_helmet',['hybrid_solar_helmet','ultimate_solar_panel'])
shaped('mt_core','PRP|P P|PRP',{'P':'irradiant_glass_pane','R':'ic2:thick_neutron_reflector'})
shaped('molecular_transformer','MTM|CcC|MTM',{'M':'ic2:advanced_machine','T':'ic2:ev_transformer','C':'ic2:advanced_circuit','c':'mt_core'})
shaped('irradiant_uranium',' G |GUG| G ',{'G':'minecraft:glowstone_dust','U':'#c:ingots/uranium'})
shaped('irradiant_glass_pane','GGG|UDU|GGG',{'G':'ic2:reinforced_glass','U':'irradiant_uranium','D':'minecraft:glowstone_dust'})
shaped('enriched_sunnarium','UUU|USU|UUU',{'U':'irradiant_uranium','S':'sunnarium'})
shaped('enriched_sunnarium_alloy',' S |SAS| S ',{'S':'enriched_sunnarium','A':'sunnarium_alloy'})
shaped('sunnarium_alloy','III|ISI|III',{'I':'ic2:iridium_plate','S':'sunnarium'})
shaped('sunnarium','SSS|SSS|SSS',{'S':'sunnarium_part'})
shapeless('double_stone_slab',['minecraft:smooth_stone_slab']*2)
# Quantum generator is creative-only in the reference.
mt=[('minecraft:wither_skeleton_skull','minecraft:nether_star',250000000),('minecraft:iron_ingot','ic2:iridium_ore',9000000),('minecraft:netherrack','minecraft:gunpowder',70000,2),('minecraft:sand','minecraft:gravel',50000),('minecraft:dirt','minecraft:clay',50000),('minecraft:charcoal','minecraft:coal',60000),('minecraft:glowstone_dust','sunnarium_part',1000000),('minecraft:glowstone','sunnarium',9000000),('minecraft:yellow_wool','minecraft:glowstone',500000),('minecraft:blue_wool','minecraft:lapis_block',500000),('minecraft:red_wool','minecraft:redstone_block',500000),('minecraft:lapis_lazuli','#c:gems/sapphire',5000000),('minecraft:redstone','#c:gems/ruby',5000000),('minecraft:coal','ic2:industrial_diamond',9000000),('ic2:industrial_diamond','minecraft:diamond',1000000),('#c:dusts/titanium','#c:dusts/chromium',500000),('#c:ingots/titanium','#c:ingots/chromium',500000),('#c:gems/quartz','#c:gems/certus_quartz',500000),('#c:ingots/copper','#c:ingots/nickel',300000),('#c:ingots/tin','#c:ingots/silver',500000),('#c:ingots/silver','minecraft:gold_ingot',500000),('#c:ingots/gold','#c:ingots/platinum',9000000)]
for n,(inp,out,energy,*count) in enumerate(mt):
 r={'type':'advanced_solar_panels:molecular_transformer','input':ingredient(inp),'energy':energy}
 if out.startswith('#'):r['output_tag']=out[1:]
 else:r['output']={'id':out if ':' in out else 'advanced_solar_panels:'+out,'count':count[0] if count else 1}
 # like the original, which only registered an OreDict conversion when both sides existed: load it only when every tag has an item
 tags=[t[1:] for t in (inp,out) if t.startswith('#')]
 if tags:r['neoforge:conditions']=[{'type':'neoforge:not','value':{'type':'neoforge:tag_empty','tag':t}} for t in tags]
 write(D/'recipe/molecular'/f'{n:02d}.json',r)
print('Generated ASP original assets, 23 crafting recipes, 22 molecular recipes')

# Original hard/easy and per-item switches. Conditions are evaluated on resource reload.
def condition(key,value=True):return {'type':'advanced_solar_panels:config','key':key,'value':value}
def conditions(path,*flags):
 data=json.loads(path.read_text());data['neoforge:conditions']=[condition(k,v) for k,v in flags];write(path,data)
for p in (D/'recipe').glob('*.json'):
 data=json.loads(p.read_text());iid=data['result']['id'].split(':')[1]
 if iid.endswith('_solar_helmet'):data['type']='advanced_solar_panels:colour_shapeless' if 'ingredients' in data else 'advanced_solar_panels:colour_shaped';write(p,data)
 if iid in ['advanced_solar_panel','hybrid_solar_panel','ultimate_solar_panel','quantum_solar_panel','advanced_solar_helmet','hybrid_solar_helmet','ultimate_solar_helmet','molecular_transformer','double_stone_slab']:conditions(p,(iid,True))
conditions(D/'recipe/advanced_solar_panel.json',('advanced_solar_panel',True),('hard',True),('simple_advanced',False))
conditions(D/'recipe/hybrid_solar_panel.json',('hybrid_solar_panel',True),('hard',True))
conditions(D/'recipe/ultimate_solar_panel.json',('ultimate_solar_panel',True),('hard',True))
shaped('advanced_solar_panel','PPP|ASA|CMC',{'P':'irradiant_glass_pane','A':'ic2:advanced_alloy','S':'ic2:solar_panel','C':'ic2:advanced_circuit','M':'ic2:advanced_machine'},suffix='_hard_simple')
conditions(D/'recipe/advanced_solar_panel_hard_simple.json',('advanced_solar_panel',True),('hard',True),('simple_advanced',True))
for simple in [False,True]:
 suffix='_normal'+('_simple' if simple else '')
 shaped('advanced_solar_panel','GGG|ASA|CMC' if simple else 'GGG|ASA|CPC',{'G':'ic2:reinforced_glass','A':'ic2:advanced_alloy','S':'ic2:solar_panel','C':'ic2:advanced_circuit',('M' if simple else 'P'):('ic2:advanced_machine' if simple else 'irradiant_reinforced_plate')},suffix=suffix)
 conditions(D/'recipe'/('advanced_solar_panel'+suffix+'.json'),('advanced_solar_panel',True),('hard',False),('simple_advanced',simple))
for panel,key,old,new in [('hybrid_solar_panel','S','enriched_sunnarium','sunnarium'),('ultimate_solar_panel','E','enriched_sunnarium_alloy','sunnarium_alloy')]:
 data=json.loads((D/'recipe'/(panel+'.json')).read_text());data['key'][key]=ingredient(new);data['neoforge:conditions']=[condition(panel),condition('hard',False)];write(D/'recipe'/(panel+'_normal.json'),data)
conditions(D/'recipe/quantum_core.json',('quantum_solar_panel',True));conditions(D/'recipe/mt_core.json',('molecular_transformer',True))

write(A/'models/item/molecular_transformer.json',{'parent':'minecraft:builtin/entity','textures':{'particle':'advanced_solar_panels:block/advanced_solar_side'},'display':{'gui':{'rotation':[30,225,0],'scale':[.65,.65,.65]},'ground':{'scale':[.5,.5,.5]},'fixed':{'scale':[.6,.6,.6]},'firstperson_righthand':{'rotation':[0,45,0],'scale':[.4,.4,.4]},'firstperson_lefthand':{'rotation':[0,225,0],'scale':[.4,.4,.4]},'thirdperson_righthand':{'rotation':[75,45,0],'translation':[0,2.5,0],'scale':[.375,.375,.375]},'thirdperson_lefthand':{'rotation':[75,225,0],'translation':[0,2.5,0],'scale':[.375,.375,.375]}}})

conditions(D/'recipe/hybrid_solar_panel_shapeless.json',('ultimate_solar_panel',True))
