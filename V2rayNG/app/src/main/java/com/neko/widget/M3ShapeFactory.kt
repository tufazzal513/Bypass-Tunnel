package com.neko.widget

import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star

object M3ShapeFactory {

    fun createM3Shape(shapeKey: String): RoundedPolygon {
        return when (shapeKey) {
            // 1. BASIC & GEOMETRIC
            "m3_circle" -> RoundedPolygon.circle()
            "m3_square" -> RoundedPolygon(4, rounding = CornerRounding(0.15f))
            "m3_slanted" -> RoundedPolygon(
                vertices = floatArrayOf(-0.5f, -1f, 1f, -1f, 0.5f, 1f, -1f, 1f),
                rounding = CornerRounding(0.2f)
            )
            "m3_arch" -> RoundedPolygon(
                vertices = floatArrayOf(-1f, -1f, 1f, -1f, 1f, 1f, -1f, 1f),
                perVertexRounding = listOf(
                    CornerRounding(1f), CornerRounding(1f),
                    CornerRounding(0f), CornerRounding(0f)
                )
            )
            "m3_semicircle" -> RoundedPolygon(
                vertices = floatArrayOf(-1f, 0f, 1f, 0f, 1f, 1f, -1f, 1f),
                perVertexRounding = listOf(
                    CornerRounding(1f), CornerRounding(1f),
                    CornerRounding(0f), CornerRounding(0f)
                )
            )
            "m3_oval" -> RoundedPolygon(
                vertices = floatArrayOf(-0.6f, -1f, 0.6f, -1f, 0.6f, 1f, -0.6f, 1f),
                rounding = CornerRounding(1f)
            )
            "m3_pill" -> RoundedPolygon(
                vertices = floatArrayOf(-1f, -0.5f, 1f, -0.5f, 1f, 0.5f, -1f, 0.5f),
                rounding = CornerRounding(0.5f)
            )
            "m3_triangle" -> RoundedPolygon(3, rounding = CornerRounding(0.2f))
            "m3_arrow" -> RoundedPolygon(
                vertices = floatArrayOf(-0.5f,-1f, 0.5f,-1f, 0.5f,0f, 1f,0f, 0f,1f, -1f,0f, -0.5f,0f),
                rounding = CornerRounding(0.1f)
            )
            "m3_fan" -> RoundedPolygon(
                vertices = floatArrayOf(-1f, -1f, 1f, -1f, 1f, 1f, -1f, 1f),
                perVertexRounding = listOf(
                    CornerRounding(0f), CornerRounding(0f),
                    CornerRounding(1f), CornerRounding(0f)
                )
            )
            "m3_diamond" -> RoundedPolygon(
                vertices = floatArrayOf(0f, -1f, 1f, 0f, 0f, 1f, -1f, 0f),
                rounding = CornerRounding(0.2f)
            )
            "m3_clamshell" -> RoundedPolygon(8, rounding = CornerRounding(0.2f))
            "m3_pentagon" -> RoundedPolygon(5, rounding = CornerRounding(0.2f))
            "m3_gem" -> RoundedPolygon(6, rounding = CornerRounding(0.2f))

            // 2. COOKIES
            "m3_cookie_4" -> RoundedPolygon.star(4, innerRadius = 0.8f, rounding = CornerRounding(0.3f))
            "m3_cookie_6" -> RoundedPolygon.star(6, innerRadius = 0.8f, rounding = CornerRounding(0.3f))
            "m3_cookie_7" -> RoundedPolygon.star(7, innerRadius = 0.8f, rounding = CornerRounding(0.3f))
            "m3_cookie_9" -> RoundedPolygon.star(9, innerRadius = 0.85f, rounding = CornerRounding(0.2f))
            "m3_cookie_12" -> RoundedPolygon.star(12, innerRadius = 0.9f, rounding = CornerRounding(0.2f))

            // 3. SUNNY & BURST
            "m3_very_sunny" -> RoundedPolygon.star(8, innerRadius = 0.4f, rounding = CornerRounding(0.1f))
            "m3_sunny" -> RoundedPolygon.star(8, innerRadius = 0.6f, rounding = CornerRounding(0.15f))
            "m3_burst" -> RoundedPolygon.star(12, innerRadius = 0.7f, rounding = CornerRounding(0.05f))
            "m3_soft_burst" -> RoundedPolygon.star(12, innerRadius = 0.85f, rounding = CornerRounding(0.2f))

            // 4. CLOVERS & FLOWER
            "m3_clover_4" -> RoundedPolygon.star(4, innerRadius = 0.4f, rounding = CornerRounding(0.4f))
            "m3_clover_8" -> RoundedPolygon.star(8, innerRadius = 0.6f, rounding = CornerRounding(0.3f))
            "m3_flower" -> RoundedPolygon.star(8, innerRadius = 0.5f, rounding = CornerRounding(0.2f))

            // 5. BOOM & PUFFY
            "m3_boom" -> RoundedPolygon.star(16, innerRadius = 0.4f, rounding = CornerRounding(0.05f))
            "m3_soft_boom" -> RoundedPolygon.star(16, innerRadius = 0.5f, rounding = CornerRounding(0.1f))
            "m3_puffy" -> RoundedPolygon.star(12, innerRadius = 0.8f, rounding = CornerRounding(0.3f))
            "m3_puffy_diamond" -> RoundedPolygon.star(4, innerRadius = 0.6f, rounding = CornerRounding(0.3f))

            // 6. CUSTOM & UNIQUE
            "m3_ghost_ish" -> RoundedPolygon(
                vertices = floatArrayOf(-1f,-1f, 1f,-1f, 1f,1f, 0.5f,0.6f, 0f,1f, -0.5f,0.6f, -1f,1f),
                perVertexRounding = listOf(
                    CornerRounding(0.6f), CornerRounding(0.6f),
                    CornerRounding(0.1f), CornerRounding(0.3f), CornerRounding(0.1f), CornerRounding(0.3f), CornerRounding(0.1f)
                )
            )
            "m3_pixel_circle" -> RoundedPolygon(16, rounding = CornerRounding(0f))
            "m3_pixel_triangle" -> RoundedPolygon(
                vertices = floatArrayOf(0f,-1f, 0.5f,-0.5f, 0.5f,0f, 1f,0f, 1f,1f, -1f,1f, -1f,0f, -0.5f,0f, -0.5f,-0.5f),
                rounding = CornerRounding(0f)
            )
            "m3_bun" -> RoundedPolygon(
                vertices = floatArrayOf(-1f,-1f, 1f,-1f, 0.7f,0f, 1f,1f, -1f,1f, -0.7f,0f),
                rounding = CornerRounding(0.4f)
            )
            "m3_heart" -> RoundedPolygon(
                vertices = floatArrayOf(0f,-0.2f, 0.8f,-0.8f, 1f,0f, 0f,1f, -1f,0f, -0.8f,-0.8f),
                perVertexRounding = listOf(
                    CornerRounding(0.1f), CornerRounding(0.8f), CornerRounding(0.4f),
                    CornerRounding(0.1f), CornerRounding(0.4f), CornerRounding(0.8f)
                )
            )

            // Fallback
            else -> RoundedPolygon.star(8, innerRadius = 0.85f, rounding = CornerRounding(0.2f))
        }
    }
}
