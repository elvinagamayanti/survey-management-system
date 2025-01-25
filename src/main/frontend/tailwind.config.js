/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../resources/templates/**/*.{html,js}"],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'sans-serif'], // Default font-family
      },
      colors: {
        primary: '#002B6A',
        icon: '#0093dd',
        font: '#003c90',
        hover: '#001d47',
        active: '00245a',
      },
      backgroundColor: {
        autofill: 'transparent',
      },
    },
  },
  variants: {
    extend: {
      backgroundColor: ['autofill'], // Mendukung pseudo-class autofill
    },
  },
  plugins: [],
}

