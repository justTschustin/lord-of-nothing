#let project(
  title: "",
  projectTitle: "",
  program: "",
  body,
) = {
  set document(title: title)
  set page(
    margin: (left: 30mm, right: 20mm, top: 20mm, bottom: 20mm),
    numbering: "I",
    number-align: end,
  )

  let body-font = "Source Serif Pro"
  let sans-font = "Source Serif Pro"

  set text(
    font: "Source Serif Pro",
    size: 12pt,
    lang: "de",
  )

  show math.equation: set text(weight: 400)

  // --- Headings ---
  show heading: set block(below: 0.85em, above: 1.75em)
  show heading: set text(font: body-font)
  set heading(numbering: "1.1")
  // Reference first-level headings as "chapters"
  show ref: it => {
    let el = it.element
    if el != none and el.func() == heading and el.level == 1 {
      [Chapter ]
      numbering(
        el.numbering,
        ..counter(heading).at(el.location())
      )
    } else {
      it
    }
  }

  // --- Code Blocks ---
  show raw.where(block: true): block.with(
    fill: luma(245),
    inset: 10pt,
    radius: 2pt,
  )

  // --- Paragraphs ---
  set par(leading: 1.2em)

  // --- Citations ---
  set cite(style: "ieee")

  // --- Quotes ---
  show quote: set pad(x: 1.5em)

  // --- Figures ---
  show figure: set text(size: 10pt)
  show figure: fig => {
    fig
    v(1em)
  }

  // --- Table of Contents ---
  {
    show heading: none
    heading(numbering: none,"Inhaltsverzeichnis")
  }

  outline(
    title: {
      text(font: body-font, 1.5em, weight: 700, "Inhaltsverzeichnis")
      v(15mm)
    },
    indent: 2em,
  )

  v(2.4fr)
  pagebreak()

// Main body.
  set par(justify: true, first-line-indent: 2em)
  set page(numbering: "1")
  counter(page).update(1)

  body
}
