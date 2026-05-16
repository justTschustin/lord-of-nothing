#import "template/thesis_template.typ": *
#import "template/common/titlepage.typ": *
#import "template/common/metadata.typ": *

#titlepage(
  title: title,
  projectTitle: projectTitle,
  university: university,
  universitySubTitle: universitySubTitle,
  program: program,
)


#show: project.with(
  title: title,
  projectTitle: projectTitle,
  program: program,
)


#show raw.where(block: true): set block(width: 100%)
#show raw.where(block: true): set text(size: 0.90em)
#set par(
  first-line-indent: 0pt,
  spacing: 2em,
)

#include "sections/01_Zielbestimmung.typ"
#pagebreak()
#include "sections/02_Produkteinsatz.typ"
#pagebreak()
#include "sections/03_Produktübersicht.typ"
#pagebreak()
#include "sections/04_Funktionale-Anforderungen.typ"
#pagebreak()
#include "sections/05_Nicht-Funktionale-Anforderungen.typ"
#pagebreak()
#include "sections/06_Produktdaten.typ"
#pagebreak()
#include "sections/07_Laufzeitumgebung.typ"
#pagebreak()
#include "sections/08_Entwicklungsumgebung.typ"
#pagebreak()
#include "sections/09_Entwicklungshistorie.typ"


