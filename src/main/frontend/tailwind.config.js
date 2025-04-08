/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../resources/templates/**/*.{html,js}"],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'sans-serif'], // Default font-family
      },
      colors: {
        primary: '#0099dd', // #002B6A
        secondary: '#75d1ff',
        icon: '#0093dd',
        font: '#003c90',
        fontdark: '#001d47',
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

