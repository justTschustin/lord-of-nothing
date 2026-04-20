 #let titlepage(
  title: "",
  projectTitle: "",
  university: "",
  universitySubTitle: "",
  program: "",
) = {
  set document(title: title)
  set page(
    margin: (left: 30mm, right: 30mm, top: 40mm, bottom: 40mm),
    numbering: none,
    number-align: center,
  )

  let body-font = "Source Serif Pro"
  let sans-font = "Source Serif Pro"

  set text(
    font: body-font,
    size: 12pt,
    lang: "en"
  )

  set par(leading: .65em)


  // --- Title Page ---
  align(center, image("../figures/logo.svg", width: 55%))

  v(5mm)
  align(center, text(font: sans-font, 2.2em, weight: 700, university))

  v(1mm)
  align(center, text(font: sans-font, 1.5em, weight: 100, universitySubTitle))

  v(7mm)

  align(center, text(font: sans-font, 1.7em, weight: 700, title))
  v(7mm)
  align(center, text(font: sans-font, 1.5em, weight: 600, projectTitle))

  v(12mm)

  align(bottom + center, pad(
    top: 3em,
    right: 20%,
    left: 20%,
    grid(
      columns: 1,
      gutter: 1em,
      strong("Gruppenmitglieder: "),
      "Justin Hesse",
      "Luca Kaden",
      "Valentin Maria Marcinek",
      "Christopher Aaron Sabbach",
    )
  ))

  pagebreak()
}
